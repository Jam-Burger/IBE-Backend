output "log_group_name" {
  description = "Name of the CloudWatch log group"
  value       = aws_cloudwatch_log_group.ecs.name
}

output "log_group_arn" {
  description = "ARN of the CloudWatch log group"
  value       = aws_cloudwatch_log_group.ecs.arn
}

output "cpu_high_alarm_arn" {
  description = "ARN of the CPU high utilization alarm"
  value       = aws_cloudwatch_metric_alarm.cpu_high.arn
}

output "memory_high_alarm_arn" {
  description = "ARN of the memory high utilization alarm"
  value       = aws_cloudwatch_metric_alarm.memory_high.arn
}

output "response_time_alarm_arn" {
  description = "ARN of the response time alarm"
  value       = aws_cloudwatch_metric_alarm.response_time.arn
}

output "error_rate_alarm_arn" {
  description = "ARN of the error rate alarm (combines 4XX and 5XX errors)"
  value       = aws_cloudwatch_metric_alarm.error_rate.arn
}

output "service_health_alarm_arn" {
  description = "ARN of the service health alarm"
  value       = aws_cloudwatch_metric_alarm.service_health.arn
} 