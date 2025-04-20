output "sampling_rule_arn" {
  description = "ARN of the X-Ray sampling rule"
  value       = aws_xray_sampling_rule.default.arn
}

output "xray_role_arn" {
  description = "ARN of the IAM role for X-Ray"
  value       = aws_iam_role.xray_api_gateway.arn
} 