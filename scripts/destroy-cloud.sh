#!/bin/bash

# Multi-Cloud Destruction Script for Hostel Management System
# Safely destroys AWS EKS, GCP GKE, or Azure AKS deployments

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
FORCE=${4:-false}

echo -e "${RED}💥 Multi-Cloud Resource Destruction${NC}"
echo -e "${RED}==================================${NC}"
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
    
    if ! command_exists terraform; then
        log_error "Terraform is not installed."
        exit 1
    fi
    
    if ! command_exists kubectl; then
        log_error "kubectl is not installed."
        exit 1
    fi
    
    log_success "Prerequisites check passed"
}

# Backup critical data
backup_data() {
    log_info "Creating data backup before destruction..."
    
    # Create backup directory
    BACKUP_DIR="./backups/pre-destroy-$(date +%Y%m%d_%H%M%S)"
    mkdir -p "$BACKUP_DIR"
    
    # Check if cluster is accessible
    if kubectl cluster-info >/dev/null 2>&1; then
        log_info "Cluster is accessible. Creating backup..."
        
        # Export Kubernetes resources
        kubectl get all -n hostel-management -o yaml > "$BACKUP_DIR/k8s-resources.yaml" 2>/dev/null || true
        kubectl get configmaps -n hostel-management -o yaml > "$BACKUP_DIR/configmaps.yaml" 2>/dev/null || true
        kubectl get secrets -n hostel-management -o yaml > "$BACKUP_DIR/secrets.yaml" 2>/dev/null || true
        kubectl get pvc -n hostel-management -o yaml > "$BACKUP_DIR/persistent-volumes.yaml" 2>/dev/null || true
        
        # Database backup (if possible)
        log_info "Attempting database backup..."
        
        # MongoDB backup
        kubectl exec -n hostel-management deployment/mongodb -- mongodump --out /tmp/backup 2>/dev/null || true
        kubectl cp hostel-management/$(kubectl get pod -n hostel-management -l app.kubernetes.io/component=mongodb -o jsonpath='{.items[0].metadata.name}'):/tmp/backup "$BACKUP_DIR/mongodb-backup" 2>/dev/null || true
        
        # MySQL backup
        kubectl exec -n hostel-management deployment/mysql -- mysqldump --all-databases > "$BACKUP_DIR/mysql-backup.sql" 2>/dev/null || true
        
        log_success "Backup created at: $BACKUP_DIR"
    else
        log_warning "Cluster is not accessible. Skipping data backup."
    fi
}

# Delete Kubernetes resources
delete_k8s_resources() {
    log_info "Deleting Kubernetes resources..."
    
    if kubectl cluster-info >/dev/null 2>&1; then
        # Delete namespace (this will delete all resources in the namespace)
        kubectl delete namespace hostel-management --timeout=300s || log_warning "Failed to delete namespace or namespace doesn't exist"
        
        # Delete any cluster-wide resources
        kubectl delete clusterrolebinding hostel-management-cluster-admin 2>/dev/null || true
        kubectl delete clusterrole hostel-management-cluster-role 2>/dev/null || true
        
        log_success "Kubernetes resources deleted"
    else
        log_warning "Cluster is not accessible. Skipping Kubernetes resource deletion."
    fi
}

# Destroy Terraform resources
destroy_terraform() {
    log_info "Destroying Terraform resources..."
    
    cd terraform
    
    # Check if terraform state exists
    if [ ! -f "terraform.tfstate" ] && [ ! -f ".terraform/terraform.tfstate" ]; then
        log_warning "No Terraform state found. Nothing to destroy."
        return
    fi
    
    # Create terraform.tfvars if it doesn't exist
    if [ ! -f "terraform.tfvars" ]; then
        cat > terraform.tfvars << EOF
cloud_provider = "$CLOUD_PROVIDER"
environment = "$ENVIRONMENT"
project_name = "$PROJECT_NAME"
EOF
    fi
    
    # Show what will be destroyed
    log_info "Planning destruction..."
    terraform plan -destroy -var-file=terraform.tfvars -out=destroy.tfplan
    
    # Confirm destruction
    if [ "$FORCE" != "true" ]; then
        echo ""
        log_warning "This will permanently destroy all cloud resources!"
        log_warning "This action cannot be undone!"
        echo ""
        read -p "Are you absolutely sure you want to continue? (type 'yes' to confirm): " -r
        if [ "$REPLY" != "yes" ]; then
            log_info "Destruction cancelled."
            exit 0
        fi
    fi
    
    # Apply destruction
    log_info "Applying destruction plan..."
    terraform apply destroy.tfplan
    
    log_success "Terraform resources destroyed"
}

# Clean up local files
cleanup_local() {
    log_info "Cleaning up local files..."
    
    # Remove Terraform files
    if [ -f "terraform/terraform.tfstate" ]; then
        mv terraform/terraform.tfstate "terraform/terraform.tfstate.backup.$(date +%Y%m%d_%H%M%S)"
        log_info "Terraform state backed up"
    fi
    
    if [ -f "terraform/terraform.tfvars" ]; then
        rm terraform/terraform.tfvars
    fi
    
    if [ -f "terraform/tfplan" ]; then
        rm terraform/tfplan
    fi
    
    if [ -f "terraform/destroy.tfplan" ]; then
        rm terraform/destroy.tfplan
    fi
    
    # Remove kubectl context (be careful with this)
    CLUSTER_NAME="$PROJECT_NAME-$ENVIRONMENT-cluster"
    kubectl config delete-context "$CLUSTER_NAME" 2>/dev/null || true
    kubectl config delete-cluster "$CLUSTER_NAME" 2>/dev/null || true
    
    log_success "Local cleanup completed"
}

# Verify destruction
verify_destruction() {
    log_info "Verifying resource destruction..."
    
    case $CLOUD_PROVIDER in
        aws)
            if command_exists aws; then
                # Check if EKS cluster exists
                if aws eks describe-cluster --name "$PROJECT_NAME-$ENVIRONMENT-cluster" >/dev/null 2>&1; then
                    log_warning "EKS cluster still exists. It may take a few minutes to fully delete."
                else
                    log_success "EKS cluster has been deleted"
                fi
                
                # Check for remaining resources
                VPC_ID=$(aws ec2 describe-vpcs --filters "Name=tag:Name,Values=$PROJECT_NAME-$ENVIRONMENT-vpc" --query 'Vpcs[0].VpcId' --output text 2>/dev/null || echo "None")
                if [ "$VPC_ID" != "None" ] && [ "$VPC_ID" != "null" ]; then
                    log_warning "VPC still exists: $VPC_ID"
                else
                    log_success "VPC has been deleted"
                fi
            fi
            ;;
        gcp)
            if command_exists gcloud; then
                # Check if GKE cluster exists
                if gcloud container clusters describe "$PROJECT_NAME-$ENVIRONMENT-cluster" --region="$REGION" >/dev/null 2>&1; then
                    log_warning "GKE cluster still exists. It may take a few minutes to fully delete."
                else
                    log_success "GKE cluster has been deleted"
                fi
            fi
            ;;
        azure)
            if command_exists az; then
                # Check if AKS cluster exists
                if az aks show --name "$PROJECT_NAME-$ENVIRONMENT-cluster" --resource-group "$PROJECT_NAME-$ENVIRONMENT" >/dev/null 2>&1; then
                    log_warning "AKS cluster still exists. It may take a few minutes to fully delete."
                else
                    log_success "AKS cluster has been deleted"
                fi
                
                # Check if resource group exists
                if az group show --name "$PROJECT_NAME-$ENVIRONMENT" >/dev/null 2>&1; then
                    log_warning "Resource group still exists. It may take a few minutes to fully delete."
                else
                    log_success "Resource group has been deleted"
                fi
            fi
            ;;
    esac
}

# Show destruction summary
show_summary() {
    echo ""
    log_success "🎯 Destruction completed!"
    echo ""
    echo -e "${BLUE}📋 Destruction Summary:${NC}"
    echo -e "   Cloud Provider:        ${GREEN}$CLOUD_PROVIDER${NC}"
    echo -e "   Environment:           ${GREEN}$ENVIRONMENT${NC}"
    echo -e "   Project Name:          ${GREEN}$PROJECT_NAME${NC}"
    echo -e "   Timestamp:             ${GREEN}$(date)${NC}"
    echo ""
    echo -e "${BLUE}💾 Backup Location:${NC}"
    if [ -d "./backups" ]; then
        echo -e "   $(ls -la ./backups/ | tail -1 | awk '{print $9}')"
    else
        echo -e "   ${YELLOW}No backup created${NC}"
    fi
    echo ""
    echo -e "${YELLOW}⚠️  Important Notes:${NC}"
    echo -e "   • All cloud resources have been destroyed"
    echo -e "   • Backups are stored locally in ./backups/"
    echo -e "   • Some resources may take additional time to fully delete"
    echo -e "   • Check your cloud console to verify complete deletion"
    echo -e "   • Any external DNS records or domain configurations need manual cleanup"
    echo ""
}

# Error cleanup function
cleanup_on_error() {
    if [ $? -ne 0 ]; then
        log_error "Destruction process failed!"
        echo ""
        log_info "Manual cleanup may be required. Check:"
        echo -e "   • Cloud provider console for remaining resources"
        echo -e "   • Terraform state file for tracked resources"
        echo -e "   • Kubernetes context and cluster configuration"
    fi
}

# Set up trap for cleanup
trap cleanup_on_error EXIT

# Main destruction flow
main() {
    check_prerequisites
    backup_data
    delete_k8s_resources
    destroy_terraform
    cleanup_local
    verify_destruction
    show_summary
}

# Show usage if no arguments
if [ $# -eq 0 ]; then
    echo "Usage: $0 <cloud_provider> [environment] [project_name] [force]"
    echo ""
    echo "Examples:"
    echo "  $0 aws prod hostel-management"
    echo "  $0 gcp staging my-hostel"
    echo "  $0 azure dev hostel-system"
    echo "  $0 aws prod hostel-management true  # Force destruction without confirmation"
    echo ""
    echo "Supported cloud providers: aws, gcp, azure"
    echo "Default environment: prod"
    echo "Default project name: hostel-management"
    echo ""
    exit 1
fi

# Run main function
main 