variable "project_name" {
  description = "Name of the project, used for resource naming"
  type        = string
}

variable "team_name" {
  description = "Name of the team, used for resource naming"
  type        = string
}

variable "environment" {
  description = "Environment name (e.g., dev, prod)"
  type        = string
}

variable "aws_region" {
  description = "AWS region where resources will be created"
  type        = string
}

variable "vpc_id" {
  description = "ID of the VPC where resources will be created"
  type        = string
}

variable "container_port" {
  description = "Port exposed by the container"
  type        = number
}

variable "task_cpu" {
  description = "CPU units for the ECS task"
  type        = number
  default     = 256
}

variable "task_memory" {
  description = "Memory for the ECS task"
  type        = number
  default     = 512
}

variable "service_desired_count" {
  description = "Desired number of tasks running in the service"
  type        = number
  default     = 1
}

variable "private_subnet_ids" {
  description = "List of private subnet IDs where ECS tasks will be launched"
  type        = list(string)
}

variable "target_group_arn" {
  description = "ARN of the ALB target group"
  type        = string
}

variable "ecs_execution_role_arn" {
  description = "ARN of the ECS execution role"
  type        = string
}

variable "ecs_task_role_arn" {
  description = "ARN of the ECS task role"
  type        = string
}

variable "alb_security_group_id" {
  description = "ID of the ALB security group"
  type        = string
}

variable "container_environment" {
  description = "Environment variables for the container"
  type        = map(string)
}

variable "tags" {
  description = "Tags to be applied to all resources"
  type        = map(string)
}

variable "image_tag" {
  description = "Tag to use for the container image"
  type        = string
  default     = "latest"
} 