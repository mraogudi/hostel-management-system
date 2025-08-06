#!/bin/bash

# Hostel Management System Backup Script
# This script creates backups of all databases and application data

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
BACKUP_DIR="${BACKUP_DIR:-./backups}"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_NAME="hostel_backup_$TIMESTAMP"
RETENTION_DAYS=${RETENTION_DAYS:-7}

# Database configuration (from .env or defaults)
MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD:-root123}
MYSQL_USER=${MYSQL_USER:-hostel_user}
MYSQL_PASSWORD=${MYSQL_PASSWORD:-hostel_password}
MONGO_ROOT_USERNAME=${MONGO_ROOT_USERNAME:-admin}
MONGO_ROOT_PASSWORD=${MONGO_ROOT_PASSWORD:-admin123}

echo -e "${BLUE}💾 Hostel Management System Backup${NC}"
echo -e "${BLUE}==================================${NC}"
echo -e "Backup Directory: ${YELLOW}$BACKUP_DIR/$BACKUP_NAME${NC}"
echo -e "Retention Period: ${YELLOW}$RETENTION_DAYS days${NC}"
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
    
    # Check if services are running
    if ! docker-compose ps | grep -q "Up"; then
        log_warning "Some services may not be running. Backup may be incomplete."
    fi
    
    log_success "Prerequisites check passed"
}

# Create backup directory
create_backup_dir() {
    log_info "Creating backup directory..."
    mkdir -p "$BACKUP_DIR/$BACKUP_NAME"
    log_success "Backup directory created: $BACKUP_DIR/$BACKUP_NAME"
}

# Backup MySQL database
backup_mysql() {
    log_info "Backing up MySQL database..."
    
    if docker-compose ps mysql | grep -q "Up"; then
        docker-compose exec -T mysql mysqldump \
            --single-transaction \
            --routines \
            --triggers \
            --all-databases \
            -u root \
            -p"$MYSQL_ROOT_PASSWORD" > "$BACKUP_DIR/$BACKUP_NAME/mysql_backup.sql"
        
        # Compress the backup
        gzip "$BACKUP_DIR/$BACKUP_NAME/mysql_backup.sql"
        log_success "MySQL database backed up successfully"
    else
        log_warning "MySQL container is not running. Skipping MySQL backup."
    fi
}

# Backup MongoDB database
backup_mongodb() {
    log_info "Backing up MongoDB database..."
    
    if docker-compose ps mongodb | grep -q "Up"; then
        # Create MongoDB backup directory
        mkdir -p "$BACKUP_DIR/$BACKUP_NAME/mongodb"
        
        # Backup using mongodump
        docker-compose exec -T mongodb mongodump \
            --username "$MONGO_ROOT_USERNAME" \
            --password "$MONGO_ROOT_PASSWORD" \
            --authenticationDatabase admin \
            --db hostel_management \
            --out /tmp/mongo_backup
        
        # Copy backup from container
        docker cp "$(docker-compose ps -q mongodb):/tmp/mongo_backup" "$BACKUP_DIR/$BACKUP_NAME/mongodb/"
        
        # Compress the backup
        tar -czf "$BACKUP_DIR/$BACKUP_NAME/mongodb_backup.tar.gz" -C "$BACKUP_DIR/$BACKUP_NAME/mongodb" .
        rm -rf "$BACKUP_DIR/$BACKUP_NAME/mongodb"
        
        log_success "MongoDB database backed up successfully"
    else
        log_warning "MongoDB container is not running. Skipping MongoDB backup."
    fi
}

# Backup Redis data
backup_redis() {
    log_info "Backing up Redis data..."
    
    if docker-compose ps redis | grep -q "Up"; then
        # Trigger Redis save
        docker-compose exec -T redis redis-cli BGSAVE
        
        # Wait a moment for save to complete
        sleep 2
        
        # Copy Redis dump file
        docker cp "$(docker-compose ps -q redis):/data/dump.rdb" "$BACKUP_DIR/$BACKUP_NAME/redis_dump.rdb" 2>/dev/null || {
            log_warning "Redis dump file not found. Redis may be empty."
        }
        
        log_success "Redis data backed up successfully"
    else
        log_warning "Redis container is not running. Skipping Redis backup."
    fi
}

# Backup application data
backup_application_data() {
    log_info "Backing up application data..."
    
    # Backup Node.js server data
    if docker-compose ps backend | grep -q "Up"; then
        mkdir -p "$BACKUP_DIR/$BACKUP_NAME/backend"
        docker cp "$(docker-compose ps -q backend):/app/data" "$BACKUP_DIR/$BACKUP_NAME/backend/" 2>/dev/null || {
            log_warning "Backend data directory not found."
        }
    fi
    
    # Backup configuration files
    cp -r .env* "$BACKUP_DIR/$BACKUP_NAME/" 2>/dev/null || true
    cp docker-compose*.yml "$BACKUP_DIR/$BACKUP_NAME/" 2>/dev/null || true
    cp -r nginx/ "$BACKUP_DIR/$BACKUP_NAME/" 2>/dev/null || true
    cp -r scripts/ "$BACKUP_DIR/$BACKUP_NAME/" 2>/dev/null || true
    
    log_success "Application data backed up successfully"
}

# Create backup manifest
create_manifest() {
    log_info "Creating backup manifest..."
    
    cat > "$BACKUP_DIR/$BACKUP_NAME/MANIFEST.txt" << EOF
Hostel Management System Backup
================================
Backup Date: $(date)
Backup Name: $BACKUP_NAME
Backup Directory: $BACKUP_DIR/$BACKUP_NAME

Contents:
- MySQL Database: $([ -f "$BACKUP_DIR/$BACKUP_NAME/mysql_backup.sql.gz" ] && echo "✅ Included" || echo "❌ Not included")
- MongoDB Database: $([ -f "$BACKUP_DIR/$BACKUP_NAME/mongodb_backup.tar.gz" ] && echo "✅ Included" || echo "❌ Not included")
- Redis Data: $([ -f "$BACKUP_DIR/$BACKUP_NAME/redis_dump.rdb" ] && echo "✅ Included" || echo "❌ Not included")
- Application Data: $([ -d "$BACKUP_DIR/$BACKUP_NAME/backend" ] && echo "✅ Included" || echo "❌ Not included")
- Configuration Files: ✅ Included

Service Status at Backup Time:
$(docker-compose ps 2>/dev/null || echo "Docker Compose not available")

Instructions for Restore:
1. Stop all services: docker-compose down
2. Restore databases using provided SQL/dump files
3. Copy configuration files back to project directory
4. Restart services: docker-compose up -d

For detailed restore instructions, see scripts/restore.sh
EOF
    
    log_success "Backup manifest created"
}

# Compress entire backup
compress_backup() {
    log_info "Compressing backup..."
    
    cd "$BACKUP_DIR"
    tar -czf "${BACKUP_NAME}.tar.gz" "$BACKUP_NAME"
    rm -rf "$BACKUP_NAME"
    
    # Calculate backup size
    BACKUP_SIZE=$(du -h "${BACKUP_NAME}.tar.gz" | cut -f1)
    
    log_success "Backup compressed: ${BACKUP_NAME}.tar.gz (${BACKUP_SIZE})"
}

# Clean old backups
cleanup_old_backups() {
    log_info "Cleaning up old backups (older than $RETENTION_DAYS days)..."
    
    find "$BACKUP_DIR" -name "hostel_backup_*.tar.gz" -type f -mtime +$RETENTION_DAYS -delete
    
    # Count remaining backups
    BACKUP_COUNT=$(find "$BACKUP_DIR" -name "hostel_backup_*.tar.gz" -type f | wc -l)
    
    log_success "Cleanup completed. $BACKUP_COUNT backup(s) retained."
}

# Show backup summary
show_summary() {
    echo ""
    log_success "🎉 Backup completed successfully!"
    echo ""
    echo -e "${BLUE}📋 Backup Details:${NC}"
    echo -e "   Location: ${GREEN}$BACKUP_DIR/${BACKUP_NAME}.tar.gz${NC}"
    echo -e "   Size: ${GREEN}$(du -h "$BACKUP_DIR/${BACKUP_NAME}.tar.gz" | cut -f1)${NC}"
    echo -e "   Created: ${GREEN}$(date)${NC}"
    echo ""
    echo -e "${BLUE}📝 Available Backups:${NC}"
    ls -lah "$BACKUP_DIR"/hostel_backup_*.tar.gz 2>/dev/null || echo "   No backups found"
    echo ""
    echo -e "${BLUE}🔄 To restore from this backup:${NC}"
    echo -e "   ${YELLOW}./scripts/restore.sh $BACKUP_DIR/${BACKUP_NAME}.tar.gz${NC}"
}

# Function to handle cleanup on exit
cleanup() {
    if [ $? -ne 0 ]; then
        log_error "Backup failed!"
        if [ -d "$BACKUP_DIR/$BACKUP_NAME" ]; then
            log_info "Cleaning up incomplete backup..."
            rm -rf "$BACKUP_DIR/$BACKUP_NAME"
        fi
    fi
}

# Set up trap for cleanup
trap cleanup EXIT

# Main backup flow
main() {
    check_prerequisites
    create_backup_dir
    backup_mysql
    backup_mongodb
    backup_redis
    backup_application_data
    create_manifest
    compress_backup
    cleanup_old_backups
    show_summary
}

# Run main function
main 