variable "project_name" {}

variable "ddb_attributes" {
  type = list(object({
    name = string
    type = string
  }))
  default = [
    # Primary Keys
    {
      name = "TenantId"
      type = "S"
    },
    {
      name = "SK"
      type = "S"
    },
    # LSI Keys
    {
      name = "ConfigType"
      type = "S"
    },
    {
      name = "UpdatedAt"
      type = "N"
    },
    # GSI Keys
    {
      name = "UserId"
      type = "S"
    },
    {
      name = "SessionExpiry"
      type = "N"
    }
  ]
}

variable "tags" {
  type    = map(string)
  default = {}
}

variable "ttl_enabled" {
  type        = bool
  default     = true
  description = "Enable TTL for configuration items that need expiration"
}

variable "ttl_attribute" {
  type        = string
  default     = "SessionExpiry"
  description = "TTL attribute name"
}