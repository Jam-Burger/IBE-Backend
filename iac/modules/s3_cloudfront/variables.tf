variable "project_name" {
  description = "Name of the project, used for resource naming"
  type        = string
}

variable "tags" {
  description = "Tags to be applied to all resources"
  type        = map(string)
}

variable "cors_allowed_origins" {
  description = "List of allowed origins for CORS (default allows all origins)"
  type        = list(string)
  default     = ["*"]
}

variable "default_ttl" {
  description = "Default time-to-live for CloudFront cache (in seconds)"
  type        = number
  default     = 3600 # 1 hour
}

variable "max_ttl" {
  description = "Maximum time-to-live for CloudFront cache (in seconds)"
  type        = number
  default     = 86400 # 24 hours
}