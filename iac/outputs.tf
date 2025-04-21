output "api_gateway_invoke_url" {
  description = "The base URL to invoke the API Gateway"
  value       = module.api_gateway.api_endpoint
}

output "redis_endpoint" {
  description = "The endpoint of the Redis cluster"
  value       = module.valkey.redis_endpoint
}

output "redis_port" {
  description = "The port of the Redis cluster"
  value       = module.valkey.redis_port
}