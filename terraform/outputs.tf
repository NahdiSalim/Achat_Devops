# Terraform Outputs for Achat Application

output "environment" {
  description = "Deployment environment"
  value       = var.environment
}

output "region" {
  description = "AWS Region"
  value       = var.aws_region
}

output "vpc_details" {
  description = "VPC Details"
  value = {
    vpc_id     = module.network.vpc_id
    vpc_cidr   = module.network.vpc_cidr
  }
}

output "database_details" {
  description = "Database Details"
  value = {
    endpoint = module.database.db_endpoint
    name     = var.db_name
  }
  sensitive = true
}

output "application_endpoints" {
  description = "Application Access Points"
  value = {
    load_balancer_url = "http://${module.load_balancer.alb_dns_name}"
    application_url   = "http://${module.load_balancer.alb_dns_name}/SpringMVC"
    swagger_ui        = "http://${module.load_balancer.alb_dns_name}/SpringMVC/swagger-ui/"
    actuator_health   = "http://${module.load_balancer.alb_dns_name}/SpringMVC/actuator/health"
  }
}

output "monitoring_urls" {
  description = "Monitoring URLs"
  value = {
    cloudwatch_dashboard = "https://console.aws.amazon.com/cloudwatch/home?region=${var.aws_region}"
  }
}

output "instance_details" {
  description = "EC2 Instance Details"
  value = {
    instance_ids  = module.compute.instance_ids
    instance_count = var.instance_count
  }
}

output "deployment_info" {
  description = "Deployment Information"
  value = {
    timestamp       = timestamp()
    terraform_version = "Terraform ${terraform.version}"
    environment     = var.environment
    docker_image    = "${var.docker_image}:${var.docker_tag}"
  }
}

