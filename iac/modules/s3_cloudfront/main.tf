resource "aws_s3_bucket" "storage" {
  bucket = "${var.project_name}-storage-bucket"
  tags = merge(var.tags, {
    Name = "${var.project_name}-storage-bucket"
  })
}

# Set ownership controls to bucket owner preferred
resource "aws_s3_bucket_ownership_controls" "storage" {
  bucket = aws_s3_bucket.storage.id

  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

# Add server-side encryption by default
resource "aws_s3_bucket_server_side_encryption_configuration" "storage" {
  bucket = aws_s3_bucket.storage.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
    bucket_key_enabled = true
  }
}

# Enable transfer acceleration if needed
resource "aws_s3_bucket_accelerate_configuration" "storage" {
  bucket = aws_s3_bucket.storage.id
  status = "Enabled"
}

resource "aws_cloudfront_origin_access_control" "oac" {
  name                              = "${var.project_name}-storage-oac"
  description                       = "Origin Access Control for S3 bucket"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

resource "aws_s3_bucket_public_access_block" "storage" {
  bucket = aws_s3_bucket.storage.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_cloudfront_distribution" "cdn" {
  origin {
    connection_attempts      = 3
    connection_timeout       = 10
    domain_name             = aws_s3_bucket.storage.bucket_regional_domain_name
    origin_id               = "S3-${var.project_name}-storage-bucket"
    origin_access_control_id = aws_cloudfront_origin_access_control.oac.id
  }

  enabled         = true
  is_ipv6_enabled = true

  default_cache_behavior {
    target_origin_id       = "S3-${var.project_name}-storage-bucket"
    viewer_protocol_policy = "redirect-to-https"
    allowed_methods        = ["GET", "HEAD", "OPTIONS"]
    cached_methods         = ["GET", "HEAD"]
    compress              = false  # Disable compression to preserve original file size

    forwarded_values {
      query_string = false
      headers      = ["Origin", "Access-Control-Request-Headers", "Access-Control-Request-Method"]
      cookies {
        forward = "none"
      }
    }

    min_ttl     = 0
    default_ttl = var.default_ttl
    max_ttl     = var.max_ttl
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }

  tags = merge(var.tags, {
    Name = "${var.project_name}-cdn"
  })
}

resource "aws_s3_bucket_policy" "storage_policy" {
  bucket = aws_s3_bucket.storage.id

  policy = templatefile("${path.module}/src/bucket-policy.json.tftpl", {
    bucket_name                = aws_s3_bucket.storage.bucket
    cloudfront_distribution_id = aws_cloudfront_distribution.cdn.id
    aws_account_id             = data.aws_caller_identity.current.account_id
  })
}

resource "aws_s3_bucket_cors_configuration" "storage" {
  bucket = aws_s3_bucket.storage.id

  cors_rule {
    allowed_headers = ["*"]
    allowed_methods = ["GET", "HEAD"]
    allowed_origins = var.cors_allowed_origins
    expose_headers  = ["ETag"]
    max_age_seconds = 3600
  }
}

data "aws_caller_identity" "current" {}