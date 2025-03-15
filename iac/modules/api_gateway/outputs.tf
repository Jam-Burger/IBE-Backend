output "api_id" {
  description = "The ID of the REST API Gateway"
  value       = aws_api_gateway_rest_api.api.id
}

output "api_arn" {
  description = "The ARN of the REST API Gateway"
  value       = aws_api_gateway_rest_api.api.arn
}

output "execution_arn" {
  description = "The execution ARN of the REST API Gateway"
  value       = aws_api_gateway_rest_api.api.execution_arn
}

output "rest_api_id" {
  description = "The ID of the REST API"
  value       = aws_api_gateway_rest_api.api.id
}

output "api_endpoint" {
  description = "The endpoint URL of the API Gateway"
  value       = aws_api_gateway_stage.api.invoke_url
} 