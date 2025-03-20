variable "project_name" {
  description = "Base name of the project (without environment) for the shared API Gateway"
  type        = string
}

variable "stage_name" {
  description = "Name of the API Gateway stage (e.g., dev, prod)"
  type        = string
}

variable "alb_dns_name" {
  description = "DNS name of the ALB"
  type        = string
}

variable "tags" {
  description = "Tags to apply to all resources"
  type        = map(string)
}

variable "allowed_origins" {
  description = "List of allowed CORS origins (e.g., ['https://example.com', 'https://app.example.com'])"
  type        = list(string)
}