# Terraform Configuration for Achat Application Infrastructure

terraform {
  required_version = ">= 1.0"
  
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0"
    }
  }

  # Backend configuration for state management
  backend "s3" {
    bucket         = "achat-terraform-state"
    key            = "achat/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "achat-terraform-locks"
  }
}

# AWS Provider Configuration
provider "aws" {
  region = var.aws_region
  
  default_tags {
    tags = {
      Project     = "Achat"
      Environment = var.environment
      ManagedBy   = "Terraform"
    }
  }
}

# Docker Provider Configuration
provider "docker" {
  host = var.docker_host
}

# Local Variables
locals {
  app_name = "achat"
  common_tags = {
    Application = local.app_name
    Environment = var.environment
    Terraform   = "true"
  }
}

# Modules

# Network Module - VPC, Subnets, Security Groups
module "network" {
  source = "./modules/network"
  
  vpc_cidr             = var.vpc_cidr
  availability_zones   = var.availability_zones
  public_subnet_cidrs  = var.public_subnet_cidrs
  private_subnet_cidrs = var.private_subnet_cidrs
  environment          = var.environment
  app_name             = local.app_name
}

# Database Module - RDS MySQL
module "database" {
  source = "./modules/database"
  
  vpc_id                = module.network.vpc_id
  private_subnet_ids    = module.network.private_subnet_ids
  db_security_group_ids = [module.network.db_security_group_id]
  
  db_name               = var.db_name
  db_username           = var.db_username
  db_password           = var.db_password
  db_instance_class     = var.db_instance_class
  db_allocated_storage  = var.db_allocated_storage
  
  environment = var.environment
  app_name    = local.app_name
}

# Compute Module - EC2 instances for application
module "compute" {
  source = "./modules/compute"
  
  vpc_id                 = module.network.vpc_id
  public_subnet_ids      = module.network.public_subnet_ids
  app_security_group_ids = [module.network.app_security_group_id]
  
  instance_type          = var.instance_type
  instance_count         = var.instance_count
  key_name               = var.key_name
  docker_image           = var.docker_image
  docker_tag             = var.docker_tag
  
  db_endpoint            = module.database.db_endpoint
  db_name                = var.db_name
  db_username            = var.db_username
  db_password            = var.db_password
  
  environment = var.environment
  app_name    = local.app_name
}

# Load Balancer Module
module "load_balancer" {
  source = "./modules/load_balancer"
  
  vpc_id                = module.network.vpc_id
  public_subnet_ids     = module.network.public_subnet_ids
  alb_security_group_id = module.network.alb_security_group_id
  
  target_instance_ids = module.compute.instance_ids
  
  environment = var.environment
  app_name    = local.app_name
}

# Monitoring Module - CloudWatch
module "monitoring" {
  source = "./modules/monitoring"
  
  instance_ids = module.compute.instance_ids
  db_instance_id = module.database.db_instance_id
  alb_arn_suffix = module.load_balancer.alb_arn_suffix
  
  environment = var.environment
  app_name    = local.app_name
}

# Docker Containers (Local deployment)
resource "docker_network" "achat_network" {
  name = "achat-network"
}

resource "docker_container" "mysql" {
  count = var.deploy_local ? 1 : 0
  
  image = "mysql:8.0"
  name  = "achat-mysql"
  
  env = [
    "MYSQL_ROOT_PASSWORD=${var.db_password}",
    "MYSQL_DATABASE=${var.db_name}",
    "MYSQL_USER=${var.db_username}",
    "MYSQL_PASSWORD=${var.db_password}"
  ]
  
  ports {
    internal = 3306
    external = 3306
  }
  
  networks_advanced {
    name = docker_network.achat_network.name
  }
  
  volumes {
    volume_name    = "mysql_data"
    container_path = "/var/lib/mysql"
  }
}

resource "docker_container" "app" {
  count = var.deploy_local ? 1 : 0
  
  image = "${var.docker_image}:${var.docker_tag}"
  name  = "achat-app"
  
  depends_on = [docker_container.mysql]
  
  env = [
    "SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/${var.db_name}",
    "SPRING_DATASOURCE_USERNAME=${var.db_username}",
    "SPRING_DATASOURCE_PASSWORD=${var.db_password}"
  ]
  
  ports {
    internal = 8089
    external = 8089
  }
  
  networks_advanced {
    name = docker_network.achat_network.name
  }
}

# Outputs
output "vpc_id" {
  description = "VPC ID"
  value       = module.network.vpc_id
}

output "alb_dns_name" {
  description = "Application Load Balancer DNS Name"
  value       = module.load_balancer.alb_dns_name
}

output "db_endpoint" {
  description = "Database Endpoint"
  value       = module.database.db_endpoint
  sensitive   = true
}

output "application_url" {
  description = "Application URL"
  value       = "http://${module.load_balancer.alb_dns_name}/SpringMVC"
}

