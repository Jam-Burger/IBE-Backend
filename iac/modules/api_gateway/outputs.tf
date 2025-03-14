output "api_id" {
  description = "The ID of the REST API Gateway"
  value       = aws_api_gateway_rest_api.api.id
}

output "api_arn" {
  description = "The ARN of the REST API Gateway"
  value       = aws_api_gateway_rest_api.api.arn
}

output "api_endpoint" {
  description = "The REST API endpoint URL"
  value       = "${aws_api_gateway_rest_api.api.execution_arn}/${aws_api_gateway_stage.api.stage_name}"
}

output "execution_arn" {
  description = "The execution ARN of the REST API Gateway"
  value       = aws_api_gateway_rest_api.api.execution_arn
} 