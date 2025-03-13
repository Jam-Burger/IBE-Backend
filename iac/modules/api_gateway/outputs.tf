output "api_id" {
  description = "The ID of the HTTP API Gateway"
  value       = aws_apigatewayv2_api.api.id
}

output "api_arn" {
  description = "The ARN of the HTTP API Gateway"
  value       = aws_apigatewayv2_api.api.arn
}

output "api_endpoint" {
  description = "The HTTP API endpoint URL"
  value       = aws_apigatewayv2_api.api.api_endpoint
}

output "execution_arn" {
  description = "The execution ARN of the HTTP API Gateway"
  value       = aws_apigatewayv2_api.api.execution_arn
} 