variable "project_name" {
  description = "Name prefix for all resources"
  type        = string
}

variable "container_environment" {
  description = "Environment variables for the Lambda functions"
  type        = map(string)
}

variable "tags" {
  description = "Tags to be applied to all resources"
  type        = map(string)
}

variable "s3_bucket_arn" {
  description = "ARN of the S3 bucket for template triggers"
  type        = string
}

variable "s3_bucket_id" {
  description = "ID of the S3 bucket for template triggers"
  type        = string
} 