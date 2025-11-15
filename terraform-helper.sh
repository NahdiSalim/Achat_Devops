#!/bin/bash

# Terraform Helper Script for Jenkins
# This script helps run Terraform commands inside Jenkins

set -e

TERRAFORM_DIR="terraform"
ACTION=$1

usage() {
    echo "Usage: $0 {init|validate|plan|apply|destroy|output|show}"
    echo ""
    echo "Commands:"
    echo "  init      - Initialize Terraform"
    echo "  validate  - Validate Terraform configuration"
    echo "  plan      - Create execution plan"
    echo "  apply     - Apply changes"
    echo "  destroy   - Destroy infrastructure"
    echo "  output    - Show outputs"
    echo "  show      - Show current state"
    exit 1
}

if [ -z "$ACTION" ]; then
    usage
fi

cd $TERRAFORM_DIR

case $ACTION in
    init)
        echo "🔧 Initializing Terraform..."
        terraform init -upgrade
        ;;
    validate)
        echo "✅ Validating Terraform configuration..."
        terraform validate
        ;;
    plan)
        echo "📋 Creating Terraform plan..."
        terraform plan -out=tfplan
        echo "✅ Plan created: tfplan"
        ;;
    apply)
        echo "🚀 Applying Terraform changes..."
        if [ -f "tfplan" ]; then
            terraform apply tfplan
        else
            terraform apply -auto-approve
        fi
        echo "✅ Changes applied!"
        ;;
    destroy)
        echo "💥 Destroying infrastructure..."
        terraform destroy -auto-approve
        echo "✅ Infrastructure destroyed!"
        ;;
    output)
        echo "📊 Terraform outputs:"
        terraform output
        ;;
    show)
        echo "📄 Current Terraform state:"
        terraform show
        ;;
    *)
        echo "❌ Unknown action: $ACTION"
        usage
        ;;
esac

