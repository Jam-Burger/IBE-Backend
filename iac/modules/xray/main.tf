resource "aws_xray_group" "this" {
  group_name        = "${var.project_name}-group"
  filter_expression = "service(\"${var.project_name}-*\") OR annotation.service = \"${var.project_name}*\""

  tags = var.tags
}

resource "aws_xray_sampling_rule" "critical_paths" {
  rule_name      = "${var.project_name}-crit-paths"
  priority       = 1
  reservoir_size = 1
  fixed_rate     = 1.0
  host           = "*"
  http_method    = "*"
  url_path       = "/api/*/bookings/*"
  version        = 1
  service_name   = "*"
  service_type   = "*"
  resource_arn   = "*"

  tags = var.tags
}

resource "aws_xray_sampling_rule" "errors" {
  rule_name      = "${var.project_name}-errors"
  priority       = 2  # Second priority
  version        = 1
  reservoir_size = 1
  fixed_rate     = 1.0  # Sample 100% of error responses
  host           = "*"
  http_method    = "*"
  url_path       = "/*"
  service_name   = "*"
  service_type   = "*"
  resource_arn   = "*"
  
  attributes = {
    Environment = var.environment
    StatusCode  = "4XX,5XX"
  }
}

resource "aws_xray_sampling_rule" "health_check" {
  rule_name      = "${var.project_name}-health-check"
  priority       = 100  # Lower priority
  version        = 1
  reservoir_size = 0
  fixed_rate     = 0.0  # Don't sample health checks
  host           = "*"
  http_method    = "GET"
  url_path       = "/health"
  service_name   = "*"
  service_type   = "*"
  resource_arn   = "*"

  attributes = {
    Environment = var.environment
  }
}

resource "aws_xray_sampling_rule" "default" {
  rule_name      = "${var.project_name}-default"
  priority       = 1000
  reservoir_size = 1
  fixed_rate     = 0.05
  host           = "*"
  http_method    = "*"
  url_path       = "/*"
  version        = 1
  service_name   = "*"
  service_type   = "*"
  resource_arn   = "*"

  tags = var.tags
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