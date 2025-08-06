#!/bin/bash

# Individual Application Deployment Script
# Deploy specific applications or all applications independently

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
APP_NAME=${1:-all}
NAMESPACE=${2:-hostel-management}
ACTION=${3:-apply}  # apply, delete, restart

echo -e "${BLUE}🚀 Individual Application Deployment${NC}"
echo -e "${BLUE}=================================${NC}"
echo -e "Application: ${YELLOW}$APP_NAME${NC}"
echo -e "Namespace: ${YELLOW}$NAMESPACE${NC}"
echo -e "Action: ${YELLOW}$ACTION${NC}"
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

# Function to check if kubectl is available
check_kubectl() {
    if ! command -v kubectl >/dev/null 2>&1; then
        log_error "kubectl is not installed or not in PATH"
        exit 1
    fi
    
    if ! kubectl cluster-info >/dev/null 2>&1; then
        log_error "Cannot connect to Kubernetes cluster"
        exit 1
    fi
}

# Function to deploy infrastructure components
deploy_infrastructure() {
    log_info "Deploying infrastructure components..."
    
    # Create namespace
    kubectl apply -f ../k8s/namespace.yaml
    
    # Apply ConfigMaps and Secrets
    kubectl apply -f ../k8s/configmap.yaml
    kubectl apply -f ../k8s/secrets.yaml
    
    # Apply Persistent Volumes
    kubectl apply -f ../k8s/persistent-volumes.yaml
    
    # Deploy databases
    kubectl apply -f ../k8s/mongodb.yaml
    kubectl apply -f ../k8s/mysql.yaml
    kubectl apply -f ../k8s/redis.yaml
    
    # Deploy Nginx load balancer
    kubectl apply -f ../k8s/ingress.yaml
    
    log_success "Infrastructure components deployed"
}

# Function to deploy specific application
deploy_application() {
    local app=$1
    log_info "Deploying application: $app"
    
    case $app in
        frontend)
            kubectl apply -f ../k8s/apps/frontend/
            ;;
        backend)
            kubectl apply -f ../k8s/apps/backend/
            ;;
        springboot-mongodb)
            kubectl apply -f ../k8s/apps/springboot-mongodb/
            ;;
        springboot-mysql)
            kubectl apply -f ../k8s/apps/springboot-mysql/
            ;;
        *)
            log_error "Unknown application: $app"
            return 1
            ;;
    esac
    
    log_success "Application $app deployed"
}

# Function to delete specific application
delete_application() {
    local app=$1
    log_info "Deleting application: $app"
    
    case $app in
        frontend)
            kubectl delete -f ../k8s/apps/frontend/ --ignore-not-found=true
            ;;
        backend)
            kubectl delete -f ../k8s/apps/backend/ --ignore-not-found=true
            ;;
        springboot-mongodb)
            kubectl delete -f ../k8s/apps/springboot-mongodb/ --ignore-not-found=true
            ;;
        springboot-mysql)
            kubectl delete -f ../k8s/apps/springboot-mysql/ --ignore-not-found=true
            ;;
        *)
            log_error "Unknown application: $app"
            return 1
            ;;
    esac
    
    log_success "Application $app deleted"
}

# Function to restart specific application
restart_application() {
    local app=$1
    log_info "Restarting application: $app"
    
    kubectl rollout restart deployment/$app -n $NAMESPACE
    kubectl rollout status deployment/$app -n $NAMESPACE --timeout=300s
    
    log_success "Application $app restarted"
}

# Function to get application status
get_application_status() {
    local app=$1
    
    echo ""
    log_info "Status for application: $app"
    echo ""
    
    # Get pods
    echo -e "${BLUE}Pods:${NC}"
    kubectl get pods -l app.kubernetes.io/component=$app -n $NAMESPACE
    
    # Get services
    echo ""
    echo -e "${BLUE}Services:${NC}"
    kubectl get services -l app.kubernetes.io/component=$app -n $NAMESPACE
    
    # Get HPA
    echo ""
    echo -e "${BLUE}HPA:${NC}"
    kubectl get hpa -l app.kubernetes.io/component=$app -n $NAMESPACE
    
    echo ""
}

# Function to show logs for application
show_application_logs() {
    local app=$1
    log_info "Showing logs for application: $app"
    
    kubectl logs -l app.kubernetes.io/component=$app -n $NAMESPACE --tail=50
}

# Function to deploy all applications
deploy_all_applications() {
    log_info "Deploying all applications..."
    
    # Deploy infrastructure first
    deploy_infrastructure
    
    # Wait for databases to be ready
    log_info "Waiting for databases to be ready..."
    kubectl wait --for=condition=Ready pod -l app.kubernetes.io/component=mongodb -n $NAMESPACE --timeout=300s
    kubectl wait --for=condition=Ready pod -l app.kubernetes.io/component=mysql -n $NAMESPACE --timeout=300s
    kubectl wait --for=condition=Ready pod -l app.kubernetes.io/component=redis -n $NAMESPACE --timeout=300s
    
    # Deploy applications
    deploy_application "backend"
    deploy_application "springboot-mongodb"
    deploy_application "springboot-mysql"
    deploy_application "frontend"
    
    # Wait for all applications to be ready
    log_info "Waiting for applications to be ready..."
    kubectl wait --for=condition=Ready pod -l app.kubernetes.io/name=hostel-management-system -n $NAMESPACE --timeout=600s
    
    log_success "All applications deployed successfully"
}

# Function to delete all applications
delete_all_applications() {
    log_info "Deleting all applications..."
    
    delete_application "frontend"
    delete_application "backend"
    delete_application "springboot-mongodb"
    delete_application "springboot-mysql"
    
    log_success "All applications deleted"
}

# Function to show status for all applications
show_all_status() {
    log_info "Status for all applications:"
    
    echo ""
    echo -e "${BLUE}All Pods:${NC}"
    kubectl get pods -n $NAMESPACE
    
    echo ""
    echo -e "${BLUE}All Services:${NC}"
    kubectl get services -n $NAMESPACE
    
    echo ""
    echo -e "${BLUE}All HPAs:${NC}"
    kubectl get hpa -n $NAMESPACE
    
    echo ""
    echo -e "${BLUE}Load Balancer:${NC}"
    kubectl get service nginx-lb-service -n $NAMESPACE
    
    echo ""
}

# Main execution logic
main() {
    check_kubectl
    
    case $ACTION in
        apply)
            if [ "$APP_NAME" = "all" ]; then
                deploy_all_applications
                show_all_status
            else
                deploy_application "$APP_NAME"
                get_application_status "$APP_NAME"
            fi
            ;;
        delete)
            if [ "$APP_NAME" = "all" ]; then
                delete_all_applications
            else
                delete_application "$APP_NAME"
            fi
            ;;
        restart)
            if [ "$APP_NAME" = "all" ]; then
                restart_application "frontend"
                restart_application "backend"
                restart_application "springboot-mongodb"
                restart_application "springboot-mysql"
            else
                restart_application "$APP_NAME"
            fi
            ;;
        status)
            if [ "$APP_NAME" = "all" ]; then
                show_all_status
            else
                get_application_status "$APP_NAME"
            fi
            ;;
        logs)
            if [ "$APP_NAME" = "all" ]; then
                log_error "Cannot show logs for all applications. Please specify one: frontend, backend, springboot-mongodb, springboot-mysql"
                exit 1
            else
                show_application_logs "$APP_NAME"
            fi
            ;;
        *)
            log_error "Unknown action: $ACTION"
            echo ""
            echo "Usage: $0 <app_name> [namespace] [action]"
            echo ""
            echo "Applications:"
            echo "  all                 - All applications"
            echo "  frontend           - React frontend"
            echo "  backend            - Node.js backend"
            echo "  springboot-mongodb - Spring Boot + MongoDB"
            echo "  springboot-mysql   - Spring Boot + MySQL"
            echo ""
            echo "Actions:"
            echo "  apply              - Deploy application(s)"
            echo "  delete             - Delete application(s)"
            echo "  restart            - Restart application(s)"
            echo "  status             - Show status"
            echo "  logs               - Show logs (single app only)"
            echo ""
            echo "Examples:"
            echo "  $0 frontend                           # Deploy frontend"
            echo "  $0 backend hostel-management delete   # Delete backend"
            echo "  $0 all hostel-management restart      # Restart all apps"
            echo "  $0 frontend hostel-management status  # Show frontend status"
            echo "  $0 backend hostel-management logs     # Show backend logs"
            exit 1
            ;;
    esac
}

# Run main function
main 