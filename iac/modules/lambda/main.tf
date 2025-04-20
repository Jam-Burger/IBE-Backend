locals {
  lambda_role_name = "${var.project_name}-lambda-role"
}

# IAM role for Lambda functions
resource "aws_iam_role" "lambda_role" {
  name = local.lambda_role_name

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })

  tags = var.tags
}

# Basic Lambda execution policy
resource "aws_iam_role_policy_attachment" "lambda_basic" {
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
  role       = aws_iam_role.lambda_role.name
}

# Additional permissions for S3 trigger
resource "aws_iam_role_policy" "lambda_s3" {
  name = "${var.project_name}-lambda-s3-policy"
  role = aws_iam_role.lambda_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:GetObject",
          "s3:ListBucket"
        ]
        Resource = [
          var.s3_bucket_arn,
          "${var.s3_bucket_arn}/*"
        ]
      }
    ]
  })
}

# Install dependencies for both Lambda functions
resource "null_resource" "lambda_dependencies" {
  triggers = {
    housekeeping_package_json = filemd5("${path.module}/housekeeping_service/package.json")
    promotional_package_json  = filemd5("${path.module}/promotional_email_sender/package.json")
    build_script              = filemd5("${path.module}/build.sh")
  }

  provisioner "local-exec" {
    working_dir = path.module
    command     = "chmod +x build.sh && ./build.sh"
  }
}

# Housekeeping Service Lambda
resource "aws_lambda_function" "housekeeping_service" {
  depends_on = [null_resource.lambda_dependencies]

  filename      = "${path.module}/housekeeping_service.zip"
  function_name = "${var.project_name}-housekeeping-service"
  role          = aws_iam_role.lambda_role.arn
  handler       = "index.handler"
  runtime       = "nodejs22.x"
  timeout       = 60
  memory_size   = 256

  environment {
    variables = {
      DB_HOST         = split(":", split("jdbc:postgresql://", var.container_environment.DB_URL)[1])[0]
      DB_PORT         = split("/", split(":", split("jdbc:postgresql://", var.container_environment.DB_URL)[1])[1])[0]
      DB_NAME         = split("/", var.container_environment.DB_URL)[length(split("/", var.container_environment.DB_URL)) - 1]
      DB_USER         = var.container_environment.DB_USERNAME
      DB_PASSWORD     = var.container_environment.DB_PASSWORD
      GRAPHQL_API_URL = var.container_environment.GRAPHQL_API_URL
      GRAPHQL_API_KEY = var.container_environment.GRAPHQL_API_KEY
      MAIL_USERNAME   = var.container_environment.MAIL_USERNAME
      MAIL_PASSWORD   = var.container_environment.MAIL_PASSWORD
    }
  }

  tags = var.tags
}

# EventBridge rule for daily trigger
resource "aws_cloudwatch_event_rule" "daily_trigger" {
  name                = "${var.project_name}-daily-housekeeping"
  description         = "Triggers housekeeping service daily at 8 AM"
  schedule_expression = "cron(0 8 * * ? *)"
  tags                = var.tags
}

resource "aws_cloudwatch_event_target" "lambda_target" {
  rule      = aws_cloudwatch_event_rule.daily_trigger.name
  target_id = "TriggerHousekeepingLambda"
  arn       = aws_lambda_function.housekeeping_service.arn
}

resource "aws_lambda_permission" "allow_eventbridge" {
  statement_id  = "AllowEventBridgeInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.housekeeping_service.function_name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.daily_trigger.arn
}

# Promotional Email Sender Lambda
resource "aws_lambda_function" "promotional_email_sender" {
  depends_on = [null_resource.lambda_dependencies]

  filename      = "${path.module}/promotional_email_sender.zip"
  function_name = "${var.project_name}-promotional-email-sender"
  role          = aws_iam_role.lambda_role.arn
  handler       = "index.handler"
  runtime       = "nodejs22.x"
  timeout       = 10
  memory_size   = 256

  environment {
    variables = {
      DB_HOST         = split(":", split("jdbc:postgresql://", var.container_environment.DB_URL)[1])[0]
      DB_PORT         = split("/", split(":", split("jdbc:postgresql://", var.container_environment.DB_URL)[1])[1])[0]
      DB_NAME         = split("/", var.container_environment.DB_URL)[length(split("/", var.container_environment.DB_URL)) - 1]
      DB_USER         = var.container_environment.DB_USERNAME
      DB_PASSWORD     = var.container_environment.DB_PASSWORD
      GRAPHQL_API_URL = var.container_environment.GRAPHQL_API_URL
      GRAPHQL_API_KEY = var.container_environment.GRAPHQL_API_KEY
      MAIL_USERNAME   = var.container_environment.MAIL_USERNAME
      MAIL_PASSWORD   = var.container_environment.MAIL_PASSWORD
    }
  }

  tags = var.tags
}

# Allow S3 to invoke Lambda - This MUST be created before the S3 notification
resource "aws_lambda_permission" "allow_s3" {
  statement_id  = "AllowS3Invoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.promotional_email_sender.function_name
  principal     = "s3.amazonaws.com"
  source_arn    = var.s3_bucket_arn
}

# S3 bucket notification for promotional email sender
resource "aws_s3_bucket_notification" "bucket_notification" {
  depends_on = [aws_lambda_permission.allow_s3]
  bucket     = var.s3_bucket_id

  lambda_function {
    lambda_function_arn = aws_lambda_function.promotional_email_sender.arn
    events              = ["s3:ObjectCreated:*"]
    filter_prefix       = "1/templates/"
    filter_suffix       = ".json"
  }
}