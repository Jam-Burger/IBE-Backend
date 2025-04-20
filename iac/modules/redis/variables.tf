variable "project_name" {
  description = "Name of the project"
  type        = string
}

variable "environment" {
  description = "Environment name"
  type        = string
}

variable "vpc_id" {
  description = "VPC ID where Redis will be deployed"
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

variable "node_type" {
  description = "The compute and memory capacity of the nodes in the cluster"
  type        = string
}

variable "num_cache_clusters" {
  description = "Number of cache clusters in the replication group"
  type        = number
}

variable "tags" {
  description = "A map of tags to add to all resources"
  type        = map(string)
} 