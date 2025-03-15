variable "project_name" {
  description = "Name of the project"
  type        = string
}

variable "team_name" {
  description = "Name of the team"
  type        = string
}

variable "environment" {
  description = "Environment name (e.g., dev, prod)"
  type        = string
}

variable "alb_dns_name" {
  description = "DNS name of the ALB"
  type        = string
}

variable "tags" {
  description = "Tags to apply to all resources"
  type = map(string)
}