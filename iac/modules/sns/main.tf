# SNS Topic for CloudWatch Alarms
resource "aws_sns_topic" "cloudwatch_alarms" {
  name = "${var.project_name}-cloudwatch-alarms"

  tags = var.tags
}

# Zip the Lambda function code
data "archive_file" "lambda_zip" {
  type        = "zip"
  source_dir  = "${path.module}/lambda"
  output_path = "${path.module}/lambda_function.zip"
}

# IAM Role for Lambda to publish to SNS and write logs
resource "aws_iam_role" "lambda_sns_role" {
  name = "${var.project_name}-lambda-sns-role"

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

resource "aws_iam_policy" "lambda_sns_policy" {
  name        = "${var.project_name}-lambda-sns-policy"
  description = "Policy to allow Lambda to publish to SNS and write logs"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = "arn:aws:logs:*:*:*"
      },
      {
        Effect = "Allow"
        Action = [
          "sns:Publish"
        ]
        Resource = aws_sns_topic.cloudwatch_alarms.arn
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_sns_policy_attachment" {
  role       = aws_iam_role.lambda_sns_role.name
  policy_arn = aws_iam_policy.lambda_sns_policy.arn
}

# Lambda function to format CloudWatch alarm messages for Slack
resource "aws_lambda_function" "alarm_notification" {
  function_name    = "${var.project_name}-alarm-notification"
  role             = aws_iam_role.lambda_sns_role.arn
  handler          = "index.handler"
  runtime          = "nodejs22.x"
  timeout          = 10
  filename         = data.archive_file.lambda_zip.output_path
  source_code_hash = data.archive_file.lambda_zip.output_base64sha256

  environment {
    variables = {
      SLACK_WEBHOOK_URL = var.slack_webhook_url
      SMTP_HOST         = "smtp.gmail.com"
      SMTP_PORT         = 587
      MAIL_USERNAME     = var.container_environment.MAIL_USERNAME
      MAIL_PASSWORD     = var.container_environment.MAIL_PASSWORD
      MAIL_FROM         = var.container_environment.MAIL_USERNAME
      MAIL_TO           = var.container_environment.MAIL_USERNAME
    }
  }

  tags = var.tags
}

# Subscribe Lambda to SNS
resource "aws_sns_topic_subscription" "lambda_subscription" {
  topic_arn = aws_sns_topic.cloudwatch_alarms.arn
  protocol  = "lambda"
  endpoint  = aws_lambda_function.alarm_notification.arn
}

# Lambda permission to allow SNS to invoke it
resource "aws_lambda_permission" "sns_invoke_lambda" {
  statement_id  = "AllowSNSInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.alarm_notification.function_name
  principal     = "sns.amazonaws.com"
  source_arn    = aws_sns_topic.cloudwatch_alarms.arn
}