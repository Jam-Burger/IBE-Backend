output "sns_topic_arn" {
  description = "ARN of the SNS topic for CloudWatch alarms"
  value       = aws_sns_topic.cloudwatch_alarms.arn
}

output "lambda_function_name" {
  description = "Name of the Lambda function for Slack notifications"
  value       = aws_lambda_function.alarm_notification.function_name
}