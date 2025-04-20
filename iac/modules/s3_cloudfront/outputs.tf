output "cloudfront_url" {
  description = "CloudFront distribution domain name"
  value       = "https://${aws_cloudfront_distribution.cdn.domain_name}"
}

output "cloudfront_distribution_id" {
  description = "CloudFront distribution ID"
  value       = aws_cloudfront_distribution.cdn.id
}

output "bucket_name" {
  description = "Name of the storage S3 bucket"
  value       = aws_s3_bucket.storage.bucket
}

output "bucket_arn" {
  description = "ARN of the storage S3 bucket"
  value       = aws_s3_bucket.storage.arn
}