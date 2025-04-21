output "redis_endpoint" {
  description = "The endpoint of the Redis cluster"
  value       = aws_elasticache_serverless_cache.this.endpoint[0]
}

output "redis_access_role_arn" {
  description = "The ARN of the IAM role for Redis access"
  value       = aws_iam_role.redis_access.arn
} 