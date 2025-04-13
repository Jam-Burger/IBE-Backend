variable "project_name" {
  description = "Name of the project, used for resource naming"
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

variable "public_subnet_ids" {
  description = "List of public subnet IDs for the ALB"
  type        = list(string)
}

variable "tags" {
  description = "Tags to be applied to all resources"
  type        = map(string)
}