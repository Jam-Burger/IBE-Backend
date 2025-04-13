variable "project_name" {
  description = "Name of the project, used for resource naming"
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
}

variable "task_memory" {
  description = "Memory for the ECS task"
  type        = number
}

variable "service_desired_count" {
  description = "Desired number of tasks running in the service"
  type        = number
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

variable "log_group_name" {
  description = "Name of the CloudWatch log group for ECS container logs"
  type        = string
}

variable "alb_arn_suffix" {
  description = "ARN suffix of the ALB for request-based autoscaling"
  type        = string
}

variable "target_group_arn_suffix" {
  description = "ARN suffix of the target group for request-based autoscaling"
  type        = string
}