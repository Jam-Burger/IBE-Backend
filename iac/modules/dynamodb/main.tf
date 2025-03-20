resource "aws_dynamodb_table" "config_sessions" {
  name         = "${var.project_name}-config-sessions"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "TenantId"
  range_key    = "SK"

  attribute {
    name = "TenantId"
    type = "S"
  }

  dynamic "attribute" {
    for_each = var.ddb_attributes
    content {
      name = attribute.value.name
      type = attribute.value.type
    }
  }

  ttl {
    enabled        = var.ttl_enabled
    attribute_name = var.ttl_attribute
  }

  # LSI for config queries
  local_secondary_index {
    name            = "ConfigTypeIndex"
    range_key       = "ConfigType"
    projection_type = "ALL"
  }

  local_secondary_index {
    name            = "ConfigUpdateIndex"
    range_key       = "UpdatedAt"
    projection_type = "ALL"
  }

  # GSI for session queries
  global_secondary_index {
    name            = "UserSessionIndex"
    hash_key        = "UserId"
    range_key       = "SessionExpiry"
    projection_type = "ALL"
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.project_name}-config-sessions"
    }
  )
}