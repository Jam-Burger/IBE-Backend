variable "project_name" {
  description = "Name of the project, used for resource naming"
  type        = string
}

variable "tags" {
  description = "Tags to be applied to all resources"
  type        = map(string)
}

variable "dynamodb_table_name" {
  description = "Name of the DynamoDB table to grant access to"
  type        = string
}