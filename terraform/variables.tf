# Terraform Variables for Achat Application Infrastructure

# AWS Configuration
variable "aws_region" {
  description = "AWS region for resources"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment name (dev, staging, prod)"
  type        = string
  default     = "dev"
}

# Network Configuration
variable "vpc_cidr" {
  description = "CIDR block for VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "availability_zones" {
  description = "List of availability zones"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b"]
}

variable "public_subnet_cidrs" {
  description = "CIDR blocks for public subnets"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnet_cidrs" {
  description = "CIDR blocks for private subnets"
  type        = list(string)
  default     = ["10.0.10.0/24", "10.0.11.0/24"]
}

# EC2 Configuration
variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "t2.medium"  # 2 vCPU, 4 GB RAM - good for Docker containers
}

variable "key_name" {
  description = "SSH key pair name (must exist in AWS)"
  type        = string
  default     = ""  # Leave empty if no SSH access needed
}

# Docker Configuration
variable "docker_image" {
  description = "Docker image for the application"
  type        = string
  default     = "salimnadi/achat"  # Your DockerHub repository
}

variable "docker_tag" {
  description = "Docker image tag"
  type        = string
  default     = "latest"
}
