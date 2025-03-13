locals {
  tags = {
    Creator = "team-${var.team_name}"
    Purpose = "${var.project_name}-project"
  }
}

# IAM Module
module "iam" {
  source = "./modules/iam"
  
  project_name = var.project_name
  team_name    = var.team_name
  tags         = local.tags
}

# ALB Module
module "alb" {
  source = "./modules/alb"
  
  project_name      = var.project_name
  team_name         = var.team_name
  vpc_id            = var.vpc_id
  container_port    = var.container_port
  public_subnet_ids = var.public_subnet_ids
  tags              = local.tags
}

# ECS Module
module "ecs" {
  source = "./modules/ecs"
  
  project_name            = var.project_name
  team_name              = var.team_name
  environment            = var.environment
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
  container_environment  = var.container_environment
  tags                   = local.tags
}

# API Gateway Module
module "api_gateway" {
  source = "./modules/api_gateway"
  
  project_name  = var.project_name
  team_name     = var.team_name
  alb_dns_name  = module.alb.alb_dns_name
  tags          = local.tags
} 