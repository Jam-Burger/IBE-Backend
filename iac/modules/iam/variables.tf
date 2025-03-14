variable "project_name" {
  description = "Name of the project, used for resource naming"
  type        = string
}

variable "team_name" {
  description = "Name of the team, used for resource naming"
  type        = string
}

variable "tags" {
  description = "Tags to be applied to all resources"
  type = map(string)
}

variable "environment" {
  description = "Environment name (e.g., dev, prod)"
  type        = string
}