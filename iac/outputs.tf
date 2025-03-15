output "api_gateway_invoke_url" {
  description = "The base URL to invoke the API Gateway"
  value       = module.api_gateway.api_endpoint
}