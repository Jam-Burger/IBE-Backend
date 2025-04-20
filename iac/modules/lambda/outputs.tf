output "housekeeping_service_arn" {
  description = "ARN of the housekeeping service Lambda function"
  value       = aws_lambda_function.housekeeping_service.arn
}

output "promotional_email_sender_arn" {
  description = "ARN of the promotional email sender Lambda function"
  value       = aws_lambda_function.promotional_email_sender.arn
}

output "lambda_role_arn" {
  description = "ARN of the Lambda execution role"
  value       = aws_iam_role.lambda_role.arn
}

output "housekeeping_service_function_name" {
  description = "Name of the housekeeping service Lambda function"
  value       = aws_lambda_function.housekeeping_service.function_name
}

output "promotional_email_sender_function_name" {
  description = "Name of the promotional email sender Lambda function"
  value       = aws_lambda_function.promotional_email_sender.function_name
} 