#!/bin/bash

# Multi-Cloud Deployment Script for Hostel Management System
# Supports AWS EKS, GCP GKE, and Azure AKS

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
CLOUD_PROVIDER=${1:-aws}
ENVIRONMENT=${2:-prod}
PROJECT_NAME=${3:-hostel-management}
REGION=${4:-""}

echo -e "${BLUE}🌤️  Multi-Cloud Hostel Management System Deployment${NC}"
echo -e "${BLUE}=================================================${NC}"
echo -e "Cloud Provider: ${YELLOW}$CLOUD_PROVIDER${NC}"
echo -e "Environment: ${YELLOW}$ENVIRONMENT${NC}"
echo -e "Project Name: ${YELLOW}$PROJECT_NAME${NC}"
echo ""

# Function to print colored output
log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
check_prerequisites() {
    log_info "Checking prerequisites..."
    
    # Check Terraform
    if ! command_exists terraform; then
        log_error "Terraform is not installed. Please install Terraform first."
        echo "Install from: https://developer.hashicorp.com/terraform/downloads"
        exit 1
    fi
    
    # Check kubectl
    if ! command_exists kubectl; then
        log_error "kubectl is not installed. Please install kubectl first."
        echo "Install from: https://kubernetes.io/docs/tasks/tools/"
        exit 1
    fi
    
    # Check Helm
    if ! command_exists helm; then
        log_error "Helm is not installed. Please install Helm first."
        echo "Install from: https://helm.sh/docs/intro/install/"
        exit 1
    fi
    
    # Check cloud-specific CLI tools
    case $CLOUD_PROVIDER in
        aws)
            if ! command_exists aws; then
                log_error "AWS CLI is not installed. Please install AWS CLI first."
                echo "Install from: https://aws.amazon.com/cli/"
                exit 1
            fi
            if ! aws sts get-caller-identity >/dev/null 2>&1; then
                log_error "AWS CLI is not configured. Please run 'aws configure' first."
                exit 1
            fi
            ;;
        gcp)
            if ! command_exists gcloud; then
                log_error "Google Cloud CLI is not installed. Please install gcloud first."
                echo "Install from: https://cloud.google.com/sdk/docs/install"
                exit 1
            fi
            if ! gcloud auth list --filter=status:ACTIVE --format="value(account)" | head -n1 >/dev/null 2>&1; then
                log_error "Google Cloud CLI is not authenticated. Please run 'gcloud auth login' first."
                exit 1
            fi
            ;;
        azure)
            if ! command_exists az; then
                log_error "Azure CLI is not installed. Please install Azure CLI first."
                echo "Install from: https://docs.microsoft.com/en-us/cli/azure/install-azure-cli"
                exit 1
            fi
            if ! az account show >/dev/null 2>&1; then
                log_error "Azure CLI is not logged in. Please run 'az login' first."
                exit 1
            fi
            ;;
        *)
            log_error "Unsupported cloud provider: $CLOUD_PROVIDER"
            log_info "Supported providers: aws, gcp, azure"
            exit 1
            ;;
    esac
    
    log_success "Prerequisites check passed"
}

# Initialize Terraform
init_terraform() {
    log_info "Initializing Terraform..."
    
    cd terraform
    
    # Initialize Terraform
    terraform init
    
    # Validate configuration
    terraform validate
    
    log_success "Terraform initialized successfully"
}

# Plan Terraform deployment
plan_terraform() {
    log_info "Planning Terraform deployment..."
    
    # Create terraform.tfvars file
    cat > terraform.tfvars << EOF
cloud_provider = "$CLOUD_PROVIDER"
environment = "$ENVIRONMENT"
project_name = "$PROJECT_NAME"
EOF
    
    # Add region if specified
    if [ ! -z "$REGION" ]; then
        echo "region = \"$REGION\"" >> terraform.tfvars
    fi
    
    # Run terraform plan
    terraform plan -var-file=terraform.tfvars -out=tfplan
    
    log_success "Terraform plan completed"
}

# Apply Terraform deployment
apply_terraform() {
    log_info "Applying Terraform deployment..."
    
    # Apply the plan
    terraform apply tfplan
    
    log_success "Terraform deployment completed"
}

# Configure kubectl
configure_kubectl() {
    log_info "Configuring kubectl..."
    
    # Get the kubectl configuration command from Terraform output
    KUBECTL_CONFIG_CMD=$(terraform output -raw kubectl_config_command)
    
    log_info "Running: $KUBECTL_CONFIG_CMD"
    eval $KUBECTL_CONFIG_CMD
    
    # Test kubectl connection
    if kubectl cluster-info >/dev/null 2>&1; then
        log_success "kubectl configured successfully"
    else
        log_error "Failed to configure kubectl"
        exit 1
    fi
}

# Deploy applications
deploy_applications() {
    log_info "Deploying applications..."
    
    # Wait for cluster to be ready
    log_info "Waiting for cluster to be ready..."
    kubectl wait --for=condition=Ready nodes --all --timeout=300s
    
    # Apply Kubernetes manifests
    log_info "Applying Kubernetes manifests..."
    kubectl apply -f ../k8s/
    
    # Wait for pods to be ready
    log_info "Waiting for pods to be ready..."
    kubectl wait --for=condition=Ready pods --all -n hostel-management --timeout=600s
    
    log_success "Applications deployed successfully"
}

# Get deployment information
get_deployment_info() {
    log_info "Getting deployment information..."
    
    # Get cluster information
    CLUSTER_ENDPOINT=$(terraform output -raw cluster_endpoint)
    CLUSTER_NAME=$(terraform output -raw cluster_name)
    CLUSTER_REGION=$(terraform output -raw cluster_region)
    
    # Get application URL
    APP_URL=""
    if terraform output load_balancer_ip >/dev/null 2>&1; then
        LOAD_BALANCER_IP=$(terraform output -raw load_balancer_ip)
        if [ "$LOAD_BALANCER_IP" != "pending" ]; then
            APP_URL="http://$LOAD_BALANCER_IP"
        fi
    fi
    
    echo ""
    log_success "🎉 Deployment completed successfully!"
    echo ""
    echo -e "${BLUE}📋 Deployment Information:${NC}"
    echo -e "   Cloud Provider:        ${GREEN}$CLOUD_PROVIDER${NC}"
    echo -e "   Environment:           ${GREEN}$ENVIRONMENT${NC}"
    echo -e "   Cluster Name:          ${GREEN}$CLUSTER_NAME${NC}"
    echo -e "   Cluster Region:        ${GREEN}$CLUSTER_REGION${NC}"
    echo -e "   Cluster Endpoint:      ${GREEN}$CLUSTER_ENDPOINT${NC}"
    if [ ! -z "$APP_URL" ]; then
        echo -e "   Application URL:       ${GREEN}$APP_URL${NC}"
    else
        echo -e "   Application URL:       ${YELLOW}Pending (LoadBalancer provisioning)${NC}"
    fi
    echo ""
    echo -e "${BLUE}🔐 Default Credentials:${NC}"
    echo -e "   Username: ${YELLOW}warden${NC}"
    echo -e "   Password: ${YELLOW}warden123${NC}"
    echo ""
    echo -e "${BLUE}📊 Service Status:${NC}"
    kubectl get pods -n hostel-management
    echo ""
    echo -e "${BLUE}🔍 Useful Commands:${NC}"
    echo -e "   View pods:             ${YELLOW}kubectl get pods -n hostel-management${NC}"
    echo -e "   View services:         ${YELLOW}kubectl get services -n hostel-management${NC}"
    echo -e "   View logs:             ${YELLOW}kubectl logs -f deployment/frontend -n hostel-management${NC}"
    echo -e "   Scale deployment:      ${YELLOW}kubectl scale deployment frontend --replicas=5 -n hostel-management${NC}"
    echo -e "   Port forward:          ${YELLOW}kubectl port-forward service/nginx-lb-service 8080:80 -n hostel-management${NC}"
    echo ""
    
    # Check if application is accessible
    if [ ! -z "$APP_URL" ]; then
        log_info "Testing application accessibility..."
        if curl -f "$APP_URL/health" >/dev/null 2>&1; then
            log_success "Application is accessible at $APP_URL"
        else
            log_warning "Application might still be starting up. Please wait a few minutes."
        fi
    fi
}

# Cleanup function
cleanup() {
    if [ $? -ne 0 ]; then
        log_error "Deployment failed!"
        echo ""
        log_info "To clean up resources, run:"
        echo -e "   ${YELLOW}terraform destroy -var-file=terraform.tfvars${NC}"
        echo ""
        log_info "To debug issues:"
        echo -e "   ${YELLOW}kubectl get events -n hostel-management${NC}"
        echo -e "   ${YELLOW}kubectl describe pods -n hostel-management${NC}"
    fi
}

# Set up trap for cleanup
trap cleanup EXIT

# Main deployment flow
main() {
    check_prerequisites
    init_terraform
    plan_terraform
    
    # Confirm deployment
    echo ""
    log_warning "This will create cloud resources which may incur costs."
    read -p "Do you want to continue? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        log_info "Deployment cancelled."
        exit 0
    fi
    
    apply_terraform
    configure_kubectl
    deploy_applications
    get_deployment_info
}

# Show usage if no arguments
if [ $# -eq 0 ]; then
    echo "Usage: $0 <cloud_provider> [environment] [project_name] [region]"
    echo ""
    echo "Examples:"
    echo "  $0 aws prod hostel-management us-west-2"
    echo "  $0 gcp staging my-hostel us-central1"
    echo "  $0 azure dev hostel-system eastus"
    echo ""
    echo "Supported cloud providers: aws, gcp, azure"
    echo "Default environment: prod"
    echo "Default project name: hostel-management"
    echo ""
    exit 1
fi

# Run main function
main 