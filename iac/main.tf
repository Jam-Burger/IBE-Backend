locals {
  environment = terraform.workspace

  name_prefix    = "${var.project_name}-${var.team_name}-${local.environment}"
  log_group_name = "/ecs/${local.name_prefix}"

  tags = {
    Creator     = "team-${var.team_name}"
    Purpose     = "${var.project_name}-project"
    Environment = local.environment
  }
}

# IAM Module
module "iam" {
  source = "./modules/iam"

  project_name        = local.name_prefix
  dynamodb_table_name = module.dynamodb.ddb_table_name
  tags                = local.tags
}

# ALB Module
module "alb" {
  source = "./modules/alb"

  project_name      = local.name_prefix
  vpc_id            = var.vpc_id
  container_port    = var.container_port
  public_subnet_ids = var.public_subnet_ids
  tags              = local.tags
}

# ECS Module
module "ecs" {
  source = "./modules/ecs"

  project_name           = local.name_prefix
  vpc_id                 = var.vpc_id
  container_port         = var.container_port
  private_subnet_ids     = var.private_subnet_ids
  target_group_arn       = module.alb.target_group_arn
  alb_security_group_id  = module.alb.alb_security_group_id
  ecs_execution_role_arn = module.iam.ecs_execution_role_arn
  ecs_task_role_arn      = module.iam.ecs_task_role_arn
  aws_region             = var.aws_region
  task_cpu               = var.task_cpu
  task_memory            = var.task_memory
  service_desired_count  = var.service_desired_count
  container_environment = merge(var.container_environment, {
    ENV                     = local.environment
    AWS_DYNAMODB_REGION     = var.aws_region
    AWS_DYNAMODB_TABLE_NAME = module.dynamodb.ddb_table_name
    AWS_S3_BUCKET_NAME      = module.storage.bucket_name
    AWS_CLOUDFRONT_BASE_URL = module.storage.cloudfront_url
    REDIS_HOST              = module.redis.redis_endpoint
    REDIS_PORT              = module.redis.redis_port
  })
  alb_arn_suffix          = regex("app/[^/]+/[^/]+$", module.alb.alb_arn)
  target_group_arn_suffix = regex("targetgroup/[^/]+/[^/]+$", module.alb.target_group_arn)
  log_group_name          = local.log_group_name
  tags                    = local.tags
}

# API Gateway Module
module "api_gateway" {
  source = "./modules/api_gateway"

  project_name    = local.name_prefix
  stage_name      = local.environment
  alb_dns_name    = module.alb.alb_dns_name
  allowed_origins = var.allowed_origins
  tags            = local.tags
}

# X-Ray Module
module "xray" {
  source = "./modules/xray"

  project_name = local.name_prefix
  environment  = local.environment
  tags        = local.tags
}

# SNS Module
module "sns" {
  source = "./modules/sns"

  project_name      = local.name_prefix
  slack_webhook_url = var.slack_webhook_url
  tags              = local.tags
}

# CloudWatch Module
module "cloudwatch" {
  source = "./modules/cloudwatch"

  project_name            = local.name_prefix
  log_group_name          = local.log_group_name
  ecs_cluster_name        = module.ecs.cluster_name
  ecs_service_name        = module.ecs.service_name
  alb_arn_suffix          = regex("app/[^/]+/[^/]+$", module.alb.alb_arn)
  target_group_arn_suffix = regex("targetgroup/[^/]+/[^/]+$", module.alb.target_group_arn)
  log_retention_days      = 30
  alarm_actions           = [module.sns.sns_topic_arn]
  ok_actions              = [module.sns.sns_topic_arn]
  tags                    = local.tags
}

module "dynamodb" {
  source       = "./modules/dynamodb"
  project_name = local.name_prefix
  tags         = local.tags
}

# S3 and CloudFront Module
module "storage" {
  source               = "./modules/s3_cloudfront"
  project_name         = local.name_prefix
  cors_allowed_origins = var.allowed_origins
  tags                 = local.tags
}

# Lambda Module
module "lambda" {
  source = "./modules/lambda"

  project_name = local.name_prefix
  container_environment = merge(var.container_environment, {
    ENV                     = local.environment
    AWS_DYNAMODB_REGION     = var.aws_region
    AWS_DYNAMODB_TABLE_NAME = module.dynamodb.ddb_table_name
    AWS_S3_BUCKET_NAME      = module.storage.bucket_name
    AWS_CLOUDFRONT_BASE_URL = module.storage.cloudfront_url
  })
  s3_bucket_arn = module.storage.bucket_arn
  s3_bucket_id  = module.storage.bucket_name
  tags          = local.tags
}

# Redis Module
module "redis" {
  source = "./modules/redis"

  project_name            = local.name_prefix
  environment            = local.environment
  vpc_id                 = var.vpc_id
  subnet_ids             = var.private_subnet_ids  # Using private subnets for security
  redis_security_group_id = module.ecs.redis_security_group_id
  node_type              = "cache.t4g.micro"  # Small instance for development, adjust for production
  num_cache_clusters     = 1  # Single node for development, increase for production
  tags                   = local.tags
}