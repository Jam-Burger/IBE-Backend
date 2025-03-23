output "log_group_name" {
  description = "The name of the CloudWatch log group"
  value       = aws_cloudwatch_log_group.ecs.name
}

output "log_group_arn" {
  description = "The ARN of the CloudWatch log group"
  value       = aws_cloudwatch_log_group.ecs.arn
}

output "cpu_high_alarm_arn" {
  description = "The ARN of the CPU high utilization alarm"
  value       = aws_cloudwatch_metric_alarm.cpu_high.arn
}

output "memory_high_alarm_arn" {
  description = "The ARN of the memory high utilization alarm"
  value       = aws_cloudwatch_metric_alarm.memory_high.arn
}

output "http_5xx_alarm_arn" {
  description = "The ARN of the 5XX errors alarm"
  value       = aws_cloudwatch_metric_alarm.http_5xx.arn
} 