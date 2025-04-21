variable "project_name" {
  description = "Name of the project"
  type        = string
}

variable "vpc_id" {
  description = "VPC ID where security groups will be created"
  type        = string
}

variable "container_port" {
  description = "Port exposed by the container"
  type        = number
}

variable "alb_security_group_id" {
  description = "Security group ID of the ALB"
  type        = string
}

variable "tags" {
  description = "A map of tags to add to all resources"
  type        = map(string)
} 