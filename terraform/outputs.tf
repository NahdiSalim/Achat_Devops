# Terraform Outputs for Achat Application Infrastructure

output "vpc_id" {
  description = "The ID of the VPC"
  value       = aws_vpc.main.id
}

output "vpc_cidr" {
  description = "The CIDR block of the VPC"
  value       = aws_vpc.main.cidr_block
}

output "public_subnet_ids" {
  description = "List of public subnet IDs"
  value       = [aws_subnet.public_1.id, aws_subnet.public_2.id]
}

output "internet_gateway_id" {
  description = "The ID of the Internet Gateway"
  value       = aws_internet_gateway.main.id
}

output "app_security_group_id" {
  description = "The ID of the application security group"
  value       = aws_security_group.app.id
}

output "availability_zones" {
  description = "List of availability zones used"
  value       = var.availability_zones
}

output "infrastructure_summary" {
  description = "Summary of created infrastructure"
  value = {
    environment        = var.environment
    region             = var.aws_region
    vpc_id             = aws_vpc.main.id
    vpc_cidr           = aws_vpc.main.cidr_block
    public_subnets     = [aws_subnet.public_1.id, aws_subnet.public_2.id]
    availability_zones = var.availability_zones
    security_group_id  = aws_security_group.app.id
  }
}
