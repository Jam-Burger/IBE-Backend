# HTTP API Gateway
resource "aws_apigatewayv2_api" "api" {
  name          = "${var.project_name}-${var.team_name}-api"
  protocol_type = "HTTP"
  
  tags = var.tags
}

# HTTP API Integration
resource "aws_apigatewayv2_integration" "alb" {
  api_id           = aws_apigatewayv2_api.api.id
  integration_type = "HTTP_PROXY"
  integration_uri  = "http://${var.alb_dns_name}"
  
  integration_method = "ANY"
  connection_type   = "INTERNET"
}

# HTTP API Route
resource "aws_apigatewayv2_route" "default" {
  api_id    = aws_apigatewayv2_api.api.id
  route_key = "ANY /{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.alb.id}"
}

# Default Stage
resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.api.id
  name        = "$default"
  auto_deploy = true

  tags = var.tags
}