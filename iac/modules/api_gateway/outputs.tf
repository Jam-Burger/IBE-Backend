output "rest_api_id" {
  description = "The ID of the REST API"
  value       = aws_api_gateway_rest_api.api.id
}

output "api_endpoint" {
  description = "The endpoint URL of the API Gateway"
  value       = aws_api_gateway_stage.api.invoke_url
} 