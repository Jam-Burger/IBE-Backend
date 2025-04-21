output "redis_endpoint" {
  description = "The endpoint of the Redis cluster"
  value       = aws_elasticache_replication_group.this.primary_endpoint_address
}

output "redis_port" {
  description = "The port of the Redis cluster"
  value       = aws_elasticache_replication_group.this.port
}

output "redis_access_role_arn" {
  description = "The ARN of the IAM role for Redis access"
  value       = aws_iam_role.redis_access.arn
} 