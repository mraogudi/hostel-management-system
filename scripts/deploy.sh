#!/bin/bash

# Hostel Management System Deployment Script
# This script deploys all four applications using Docker Compose

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="hostel-management"
ENVIRONMENT=${1:-production}
FORCE_REBUILD=${2:-false}

echo -e "${BLUE}🏢 Hostel Management System Deployment${NC}"
echo -e "${BLUE}======================================${NC}"
echo -e "Environment: ${YELLOW}$ENVIRONMENT${NC}"
echo -e "Force Rebuild: ${YELLOW}$FORCE_REBUILD${NC}"
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
    
    if ! command_exists docker; then
        log_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! command_exists docker-compose; then
        log_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    
    # Check if Docker daemon is running
    if ! docker info >/dev/null 2>&1; then
        log_error "Docker daemon is not running. Please start Docker first."
        exit 1
    fi
    
    log_success "Prerequisites check passed"
}

# Function to setup environment
setup_environment() {
    log_info "Setting up environment..."
    
    # Create .env file if it doesn't exist
    if [ ! -f .env ]; then
        if [ -f env.example ]; then
            cp env.example .env
            log_warning "Created .env file from env.example. Please review and update the configuration."
        else
            log_error ".env file not found and env.example doesn't exist."
            exit 1
        fi
    fi
    
    # Create necessary directories
    mkdir -p logs
    mkdir -p data/mongodb
    mkdir -p data/mysql
    mkdir -p data/redis
    mkdir -p data/backend
    
    log_success "Environment setup completed"
}

# Function to build images
build_images() {
    log_info "Building Docker images..."
    
    if [ "$FORCE_REBUILD" = "true" ]; then
        log_info "Force rebuilding all images..."
        docker-compose build --no-cache
    else
        docker-compose build
    fi
    
    log_success "Docker images built successfully"
}

# Function to start services
start_services() {
    log_info "Starting services..."
    
    case $ENVIRONMENT in
        development|dev)
            log_info "Starting in development mode..."
            docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
            ;;
        staging)
            log_info "Starting in staging mode..."
            docker-compose -f docker-compose.yml up -d
            ;;
        production|prod)
            log_info "Starting in production mode..."
            docker-compose -f docker-compose.yml up -d
            ;;
        *)
            log_error "Unknown environment: $ENVIRONMENT"
            log_info "Supported environments: development, staging, production"
            exit 1
            ;;
    esac
    
    log_success "Services started successfully"
}

# Function to wait for services to be healthy
wait_for_services() {
    log_info "Waiting for services to become healthy..."
    
    local max_attempts=30
    local attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if docker-compose ps | grep -q "unhealthy\|Exit"; then
            log_warning "Some services are not healthy yet. Waiting... (Attempt: $((attempt + 1))/$max_attempts)"
            sleep 10
            attempt=$((attempt + 1))
        else
            break
        fi
    done
    
    if [ $attempt -eq $max_attempts ]; then
        log_error "Services failed to become healthy within the timeout period"
        docker-compose ps
        exit 1
    fi
    
    log_success "All services are healthy"
}

# Function to run health checks
run_health_checks() {
    log_info "Running health checks..."
    
    # Check frontend
    if curl -f http://localhost:3000 >/dev/null 2>&1; then
        log_success "Frontend is responding"
    else
        log_warning "Frontend health check failed"
    fi
    
    # Check Node.js backend
    if curl -f http://localhost:5000/api/health >/dev/null 2>&1; then
        log_success "Node.js backend is responding"
    else
        log_warning "Node.js backend health check failed"
    fi
    
    # Check Spring Boot MongoDB
    if curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; then
        log_success "Spring Boot MongoDB is responding"
    else
        log_warning "Spring Boot MongoDB health check failed"
    fi
    
    # Check Spring Boot MySQL
    if curl -f http://localhost:8081/actuator/health >/dev/null 2>&1; then
        log_success "Spring Boot MySQL is responding"
    else
        log_warning "Spring Boot MySQL health check failed"
    fi
    
    # Check Nginx load balancer
    if curl -f http://localhost/health >/dev/null 2>&1; then
        log_success "Nginx load balancer is responding"
    else
        log_warning "Nginx load balancer health check failed"
    fi
}

# Function to show deployment summary
show_summary() {
    echo ""
    log_success "🎉 Deployment completed successfully!"
    echo ""
    echo -e "${BLUE}📋 Service URLs:${NC}"
    echo -e "   Frontend:              ${GREEN}http://localhost:3000${NC}"
    echo -e "   Node.js API:           ${GREEN}http://localhost:5000${NC}"
    echo -e "   Spring Boot MongoDB:   ${GREEN}http://localhost:8080${NC}"
    echo -e "   Spring Boot MySQL:     ${GREEN}http://localhost:8081${NC}"
    echo -e "   Load Balancer:         ${GREEN}http://localhost${NC}"
    echo ""
    echo -e "${BLUE}🗄️  Database Access:${NC}"
    echo -e "   MongoDB:               ${GREEN}mongodb://localhost:27017${NC}"
    echo -e "   MySQL:                 ${GREEN}mysql://localhost:3306${NC}"
    echo -e "   Redis:                 ${GREEN}redis://localhost:6379${NC}"
    echo ""
    echo -e "${BLUE}🔐 Default Credentials:${NC}"
    echo -e "   Username: ${YELLOW}warden${NC}"
    echo -e "   Password: ${YELLOW}warden123${NC}"
    echo ""
    echo -e "${BLUE}📊 Service Status:${NC}"
    docker-compose ps
    echo ""
    echo -e "${BLUE}📝 View logs:${NC} docker-compose logs -f [service_name]"
    echo -e "${BLUE}🛑 Stop services:${NC} docker-compose down"
    echo -e "${BLUE}🔄 Restart services:${NC} docker-compose restart"
}

# Function to handle cleanup on exit
cleanup() {
    if [ $? -ne 0 ]; then
        log_error "Deployment failed!"
        echo ""
        log_info "Checking service status..."
        docker-compose ps
        echo ""
        log_info "Recent logs:"
        docker-compose logs --tail=20
    fi
}

# Set up trap for cleanup
trap cleanup EXIT

# Main deployment flow
main() {
    check_prerequisites
    setup_environment
    build_images
    start_services
    wait_for_services
    sleep 5  # Give services a moment to fully initialize
    run_health_checks
    show_summary
}

# Run main function
main 