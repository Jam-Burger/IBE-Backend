variable "project_name" {
  description = "Name of the project"
  type        = string
}

variable "subnet_ids" {
  description = "List of subnet IDs where Redis will be deployed"
  type        = list(string)
}

variable "redis_security_group_id" {
  description = "The ID of the security group for Redis"
  type        = string
}

variable "max_data_storage" {
  description = "Maximum data storage in GB for serverless Redis"
  type        = number
  default     = 5
}

variable "max_ecpu_per_second" {
  description = "Maximum eCPU per second for serverless Redis"
  type        = number
  default     = 10000
}

variable "tags" {
  description = "A map of tags to add to all resources"
  type        = map(string)
} 