# ECS Execution Role
resource "aws_iam_role" "ecs_execution_role" {
  name = "${var.project_name}-ecs-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  tags = var.tags
}

# ECS Task Role
resource "aws_iam_role" "ecs_task_role" {
  name = "${var.project_name}-ecs-task-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })

  tags = var.tags
}

# ECS Execution Role Policy
resource "aws_iam_role_policy" "ecs_execution_role_policy" {
  name = "${var.project_name}-ecs-execution-policy"
  role = aws_iam_role.ecs_execution_role.id

  policy = templatefile("${path.module}/policies/ecs_execution_role_policy.tftpl", {
    project_name = var.project_name
  })
}

# ECS Task Role Policy
resource "aws_iam_role_policy" "ecs_task_role_policy" {
  name = "${var.project_name}-ecs-task-policy"
  role = aws_iam_role.ecs_task_role.id

  policy = templatefile("${path.module}/policies/ecs_task_role_policy.tftpl", {
    dynamodb_table_name = var.dynamodb_table_name
  })
} 