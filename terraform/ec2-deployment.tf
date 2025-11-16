# EC2 Instance for Achat Application Deployment

# Data source to get the latest Amazon Linux 2 AMI
data "aws_ami" "amazon_linux_2" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

# EC2 Instance for Application
resource "aws_instance" "achat_app" {
  ami           = data.aws_ami.amazon_linux_2.id
  instance_type = var.instance_type
  key_name      = var.key_name  # You'll need to create this in AWS Console

  subnet_id                   = aws_subnet.public_1.id
  vpc_security_group_ids      = [aws_security_group.app.id]
  associate_public_ip_address = true

  # User data script to install Docker and run application
  user_data = <<-EOF
              #!/bin/bash
              # Update system
              yum update -y
              
              # Install Docker
              amazon-linux-extras install docker -y
              systemctl start docker
              systemctl enable docker
              
              # Add ec2-user to docker group
              usermod -a -G docker ec2-user
              
              # Pull and run the application
              docker pull ${var.docker_image}:${var.docker_tag}
              
              # Run MySQL container
              docker run -d \
                --name achat-mysql \
                --restart unless-stopped \
                -e MYSQL_ROOT_PASSWORD=root \
                -e MYSQL_DATABASE=achatdb \
                -e MYSQL_USER=achat_user \
                -e MYSQL_PASSWORD=achat_password \
                -p 3306:3306 \
                mysql:8.0
              
              # Wait for MySQL to be ready
              sleep 30
              
              # Run Application container
              docker run -d \
                --name achat-app \
                --restart unless-stopped \
                --link achat-mysql:mysql \
                -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/achatdb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC \
                -e SPRING_DATASOURCE_USERNAME=achat_user \
                -e SPRING_DATASOURCE_PASSWORD=achat_password \
                -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
                -p 8089:8089 \
                ${var.docker_image}:${var.docker_tag}
              
              # Install Prometheus
              docker run -d \
                --name prometheus \
                --restart unless-stopped \
                --link achat-app:app \
                -p 9090:9090 \
                prom/prometheus:latest
              
              # Create a simple index.html with links
              mkdir -p /var/www/html
              cat > /var/www/html/index.html <<'HTML'
              <!DOCTYPE html>
              <html>
              <head>
                  <title>Achat Application - AWS Deployment</title>
                  <style>
                      body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
                      h1 { color: #232F3E; }
                      .link-box { background: #f0f0f0; padding: 20px; margin: 10px 0; border-radius: 5px; }
                      a { color: #FF9900; text-decoration: none; font-size: 18px; }
                      a:hover { text-decoration: underline; }
                      .status { color: green; font-weight: bold; }
                  </style>
              </head>
              <body>
                  <h1>🚀 Achat Application - AWS Deployment</h1>
                  <p class="status">✅ Application is running!</p>
                  
                  <div class="link-box">
                      <h3>📖 Swagger API Documentation</h3>
                      <a href="/SpringMVC/swagger-ui/" target="_blank">Open Swagger UI</a>
                      <p>Interactive API documentation and testing</p>
                  </div>
                  
                  <div class="link-box">
                      <h3>🏥 Health Check</h3>
                      <a href="/SpringMVC/actuator/health" target="_blank">Health Endpoint</a>
                      <p>Application health status</p>
                  </div>
                  
                  <div class="link-box">
                      <h3>📊 Metrics</h3>
                      <a href="/SpringMVC/actuator/prometheus" target="_blank">Prometheus Metrics</a>
                      <p>Application metrics for monitoring</p>
                  </div>
                  
                  <div class="link-box">
                      <h3>🔍 Prometheus</h3>
                      <a href=":9090" target="_blank">Prometheus Dashboard</a>
                      <p>Monitoring and metrics</p>
                  </div>
              </body>
              </html>
HTML
              
              # Install nginx to serve the landing page and proxy
              amazon-linux-extras install nginx1 -y
              
              # Configure nginx
              cat > /etc/nginx/conf.d/achat.conf <<'NGINX'
              server {
                  listen 80;
                  server_name _;
                  
                  # Landing page
                  location = / {
                      root /var/www/html;
                      index index.html;
                  }
                  
                  # Proxy to Spring Boot application
                  location /SpringMVC {
                      proxy_pass http://localhost:8089;
                      proxy_set_header Host $host;
                      proxy_set_header X-Real-IP $remote_addr;
                      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
                      proxy_set_header X-Forwarded-Proto $scheme;
                  }
              }
NGINX
              
              # Start nginx
              systemctl start nginx
              systemctl enable nginx
              
              # Log completion
              echo "Deployment completed at $(date)" >> /var/log/achat-deployment.log
              EOF

  tags = merge(
    local.common_tags,
    {
      Name = "${local.app_name}-app-instance-${var.environment}"
      Role = "application-server"
    }
  )

  # Ensure proper initialization
  lifecycle {
    create_before_destroy = true
  }
}

# Elastic IP for stable public address
resource "aws_eip" "achat_app" {
  instance = aws_instance.achat_app.id
  domain   = "vpc"

  tags = merge(
    local.common_tags,
    {
      Name = "${local.app_name}-eip-${var.environment}"
    }
  )

  depends_on = [aws_internet_gateway.main]
}

# Outputs for easy access
output "ec2_instance_id" {
  description = "EC2 Instance ID"
  value       = aws_instance.achat_app.id
}

output "ec2_public_ip" {
  description = "EC2 Instance Public IP (Elastic IP)"
  value       = aws_eip.achat_app.public_ip
}

output "swagger_url" {
  description = "Swagger UI URL"
  value       = "http://${aws_eip.achat_app.public_ip}/SpringMVC/swagger-ui/"
}

output "application_url" {
  description = "Application Base URL"
  value       = "http://${aws_eip.achat_app.public_ip}/SpringMVC"
}

output "health_check_url" {
  description = "Health Check URL"
  value       = "http://${aws_eip.achat_app.public_ip}/SpringMVC/actuator/health"
}

output "prometheus_url" {
  description = "Prometheus URL"
  value       = "http://${aws_eip.achat_app.public_ip}:9090"
}

output "access_instructions" {
  description = "How to access your application"
  value = <<-EOT
  
  ========================================
  🚀 ACHAT APPLICATION DEPLOYED TO AWS! 🚀
  ========================================
  
  📖 Swagger UI:    http://${aws_eip.achat_app.public_ip}/SpringMVC/swagger-ui/
  🏠 Landing Page:  http://${aws_eip.achat_app.public_ip}/
  🏥 Health Check:  http://${aws_eip.achat_app.public_ip}/SpringMVC/actuator/health
  📊 Prometheus:    http://${aws_eip.achat_app.public_ip}:9090
  
  📝 Instance ID:   ${aws_instance.achat_app.id}
  🌐 Public IP:     ${aws_eip.achat_app.public_ip}
  
  ⚠️  Note: Wait 2-3 minutes for application to fully start!
  
  🔐 SSH Access:
  ssh -i your-key.pem ec2-user@${aws_eip.achat_app.public_ip}
  
  📋 Check logs:
  ssh -i your-key.pem ec2-user@${aws_eip.achat_app.public_ip} "docker logs achat-app"
  
  ========================================
  EOT
}

