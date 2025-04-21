variable "project_name" {
  description = "Name of the project"
  type        = string
}

variable "slack_webhook_url" {
  description = "Webhook URL for Slack integration"
  type        = string
  sensitive   = true
}

variable "tags" {
  description = "A map of tags to add to all resources"
  type        = map(string)
  default     = {}
}

variable "container_environment" {
  description = "Environment variables for the Lambda functions"
  type        = map(string)
}