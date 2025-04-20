output "sampling_rule_arn" {
  description = "ARN of the X-Ray sampling rule"
  value       = aws_xray_sampling_rule.default.arn
}

output "xray_role_arn" {
  description = "ARN of the IAM role for X-Ray"
  value       = aws_iam_role.xray_api_gateway.arn
}

output "api_gateway_role_arn" {
  description = "ARN of the IAM role for API Gateway X-Ray tracing"
  value       = aws_iam_role.xray_api_gateway.arn
}

output "group_name" {
  description = "Name of the X-Ray group"
  value       = aws_xray_group.this.group_name
}

output "group_arn" {
  description = "ARN of the X-Ray group"
  value       = aws_xray_group.this.arn
} 