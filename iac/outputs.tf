output "api_gateway_invoke_url" {
  description = "The base URL to invoke the API Gateway"
  value       = "https://${module.api_gateway.rest_api_id}.execute-api.${var.aws_region}.amazonaws.com/${var.environment}"
}