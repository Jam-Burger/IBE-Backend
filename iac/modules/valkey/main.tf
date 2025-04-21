resource "aws_elasticache_subnet_group" "this" {
  name       = "${var.project_name}-redis-subnet-group"
  subnet_ids = var.subnet_ids
}

resource "aws_elasticache_replication_group" "this" {
  replication_group_id       = "${var.project_name}-redis"
  description                = "Redis cluster for ${var.project_name}"
  engine                     = "valkey"
  node_type                  = var.node_type
  num_cache_clusters         = var.num_cache_clusters
  port                       = 6379
  subnet_group_name          = aws_elasticache_subnet_group.this.name
  security_group_ids = [var.redis_security_group_id]
  automatic_failover_enabled = var.num_cache_clusters > 1
  multi_az_enabled           = var.num_cache_clusters > 1
  at_rest_encryption_enabled = true
  transit_encryption_enabled = true

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
          "elasticache:DescribeReplicationGroups"
        ]
        Resource = [aws_elasticache_replication_group.this.arn]
      }
    ]
  })
} 