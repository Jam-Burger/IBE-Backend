resource "aws_elasticache_serverless_cache" "this" {
  engine      = "valkey"
  name        = "${var.project_name}-redis"
  description = "Serverless Redis for ${var.project_name}"
  cache_usage_limits {
    data_storage {
      maximum = var.max_data_storage
      unit    = "GB"
    }
    ecpu_per_second {
      maximum = var.max_ecpu_per_second
    }
  }
  subnet_ids               = var.subnet_ids
  security_group_ids       = [var.redis_security_group_id]
  daily_snapshot_time      = "00:00"
  snapshot_retention_limit = 0

  tags = var.tags
}

# IAM role for services to access Redis
resource "aws_iam_role" "redis_access" {
  name = "${var.project_name}-redis-access"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  tags = var.tags
}

resource "aws_iam_role_policy" "redis_access" {
  name = "${var.project_name}-redis-access-policy"
  role = aws_iam_role.redis_access.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "elasticache:DescribeCacheClusters",
          "elasticache:DescribeServerlessCaches",
          "elasticache:DescribeServerlessCacheSnapshots"
        ]
        Resource = [aws_elasticache_serverless_cache.this.arn]
      }
    ]
  })
} 