output "ddb_table_name" {
  value = aws_dynamodb_table.config_sessions.name
}