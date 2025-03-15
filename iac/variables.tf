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

variable "vpc_cidr" {
  description = "CIDR block of the VPC"
  type        = string
}

variable "container_port" {
  description = "Port exposed by the container"
  type        = number
  default     = 8080
}

variable "public_subnet_ids" {
  description = "List of public subnet IDs for the ALB"
  type        = list(string)
}

variable "private_subnet_ids" {
  description = "List of private subnet IDs where ECS tasks will be launched"
  type        = list(string)
}

variable "task_cpu" {
  description = "CPU units for the ECS task (.25 vCPU)"
  type        = number
  default     = 256
}

variable "task_memory" {
  description = "Memory for the ECS task (512 MB)"
  type        = number
  default     = 512
}

variable "service_desired_count" {
  description = "Desired number of tasks running in the service"
  type        = number
  default     = 1
}

variable "container_environment" {
  description = "Environment variables for the container"
  type        = map(string)
  sensitive   = false
}

variable "allowed_origins" {
  description = "List of allowed CORS origins (e.g., ['https://example.com', 'https://app.example.com'])"
  type        = list(string)
} 