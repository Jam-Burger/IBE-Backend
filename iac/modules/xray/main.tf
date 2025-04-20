resource "aws_xray_sampling_rule" "default" {
  rule_name      = "${var.project_name}-sampling-rule"
  priority       = 1000
  version        = 1
  reservoir_size = 1
  fixed_rate     = 0.05  # Sample 5% of requests
  host           = "*"
  http_method    = "*"
  url_path       = "/*"
  service_name   = "*"
  service_type   = "*"
  resource_arn   = "*"

  attributes = {
    Environment = var.environment
  }
}

resource "aws_xray_encryption_config" "this" {
  type = "NONE"  # Can be changed to "KMS" if you want to use KMS encryption
}

# IAM role for API Gateway to write traces to X-Ray
resource "aws_iam_role" "xray_api_gateway" {
  name = "${var.project_name}-xray-api-gateway"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "apigateway.amazonaws.com"
        }
      }
    ]
  })

  tags = var.tags
}

resource "aws_iam_role_policy_attachment" "xray_api_gateway" {
  role       = aws_iam_role.xray_api_gateway.name
  policy_arn = "arn:aws:iam::aws:policy/AWSXRayDaemonWriteAccess"
}