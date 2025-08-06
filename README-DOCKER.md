# Hostel Management System - Docker Deployment Guide

This guide provides comprehensive instructions for deploying the Hostel Management System using Docker and Docker Compose.

## 🏗️ Architecture Overview

The system consists of four main applications:

1. **React Frontend** (Port 3000) - User interface built with React 18
2. **Node.js Backend** (Port 5000) - REST API with JSON file database
3. **Spring Boot + MongoDB** (Port 8080) - Java backend with MongoDB
4. **Spring Boot + MySQL** (Port 8081) - Java backend with MySQL database

### Additional Services:
- **MongoDB** (Port 27017) - NoSQL database
- **MySQL** (Port 3306) - Relational database  
- **Redis** (Port 6379) - Caching layer
- **Nginx** (Port 80/443) - Load balancer and reverse proxy

## 📋 Prerequisites

- **Docker** (version 20.10 or higher)
- **Docker Compose** (version 2.0 or higher)
- **Git** (for cloning the repository)
- **8GB RAM** minimum (recommended 16GB)
- **20GB** free disk space

### Installation Links:
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd hostel-management-system
```

### 2. Environment Setup
```bash
# Copy environment template
cp env.example .env

# Edit environment variables
nano .env  # or use your preferred editor
```

### 3. Deploy with Script
```bash
# Make deployment script executable
chmod +x scripts/deploy.sh

# Deploy in production mode
./scripts/deploy.sh production

# Or deploy in development mode
./scripts/deploy.sh development
```

### 4. Access the Application
- **Frontend**: http://localhost:3000
- **Load Balancer**: http://localhost
- **Node.js API**: http://localhost:5000
- **Spring Boot MongoDB**: http://localhost:8080
- **Spring Boot MySQL**: http://localhost:8081

**Default Login:**
- Username: `warden`
- Password: `warden123`

## 🔧 Manual Deployment

### 1. Environment Configuration
```bash
# Create and configure environment file
cp env.example .env
```

Edit `.env` file with your configuration:
```env
# Database Configuration
MYSQL_ROOT_PASSWORD=your_secure_password
MYSQL_USER=hostel_user
MYSQL_PASSWORD=your_secure_password

MONGO_ROOT_USERNAME=admin
MONGO_ROOT_PASSWORD=your_secure_password

# JWT Configuration  
JWT_SECRET=your_super_secret_jwt_key_change_in_production

# Application Configuration
NODE_ENV=production
REACT_APP_API_URL=http://localhost:5000/api
REACT_APP_SPRING_API_URL=http://localhost:8080/api
```

### 2. Build and Start Services
```bash
# Build all Docker images
docker-compose build

# Start all services
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f
```

### 3. Development Mode
```bash
# Start in development mode with hot reload
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
```

## 📊 Service Management

### View Service Status
```bash
docker-compose ps
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f frontend
docker-compose logs -f backend
docker-compose logs -f springboot-mongodb
docker-compose logs -f springboot-mysql
```

### Restart Services
```bash
# Restart all services
docker-compose restart

# Restart specific service
docker-compose restart frontend
```

### Stop Services
```bash
# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ DATA LOSS)
docker-compose down -v
```

### Scale Services
```bash
# Scale frontend to 3 instances
docker-compose up -d --scale frontend=3

# Scale backend to 2 instances  
docker-compose up -d --scale backend=2
```

## 🗄️ Database Management

### MongoDB Access
```bash
# Connect to MongoDB container
docker-compose exec mongodb mongosh

# Or with authentication
docker-compose exec mongodb mongosh -u admin -p admin123 --authenticationDatabase admin
```

### MySQL Access
```bash
# Connect to MySQL container
docker-compose exec mysql mysql -u root -p

# Connect to specific database
docker-compose exec mysql mysql -u hostel_user -p hostel_management
```

### Redis Access
```bash
# Connect to Redis container
docker-compose exec redis redis-cli
```

## 💾 Backup and Restore

### Automated Backup
```bash
# Make backup script executable
chmod +x scripts/backup.sh

# Create backup
./scripts/backup.sh

# Backup with custom retention (10 days)
RETENTION_DAYS=10 ./scripts/backup.sh

# Backup to custom directory
BACKUP_DIR=/path/to/backups ./scripts/backup.sh
```

### Manual Database Backup

#### MySQL Backup
```bash
docker-compose exec mysql mysqldump \
  --single-transaction \
  --routines \
  --triggers \
  --all-databases \
  -u root \
  -p > mysql_backup_$(date +%Y%m%d).sql
```

#### MongoDB Backup
```bash
docker-compose exec mongodb mongodump \
  --username admin \
  --password admin123 \
  --authenticationDatabase admin \
  --db hostel_management \
  --out /tmp/mongo_backup

docker cp $(docker-compose ps -q mongodb):/tmp/mongo_backup ./mongo_backup_$(date +%Y%m%d)
```

## 🔍 Health Checks and Monitoring

### Health Check Endpoints
- Frontend: `http://localhost:3000`
- Node.js Backend: `http://localhost:5000/api/health`
- Spring Boot MongoDB: `http://localhost:8080/actuator/health`
- Spring Boot MySQL: `http://localhost:8081/actuator/health`
- Nginx Load Balancer: `http://localhost/health`

### Container Health Status
```bash
# Check container health
docker-compose ps

# Detailed health information
docker inspect $(docker-compose ps -q) | grep -A 5 "Health"
```

### Resource Usage
```bash
# Container resource usage
docker stats

# Disk usage
docker system df

# Clean up unused resources
docker system prune -f
```

## 🛠️ Troubleshooting

### Common Issues

#### Port Conflicts
```bash
# Check what's using ports
netstat -tulpn | grep :3000
netstat -tulpn | grep :5000
netstat -tulpn | grep :8080

# Kill processes on ports
sudo lsof -ti:3000 | xargs kill -9
```

#### Container Startup Issues
```bash
# Check container logs
docker-compose logs [service-name]

# Rebuild problematic service
docker-compose build --no-cache [service-name]
docker-compose up -d [service-name]
```

#### Database Connection Issues
```bash
# Check database container status
docker-compose ps mongodb mysql

# Check database logs
docker-compose logs mongodb
docker-compose logs mysql

# Restart database containers
docker-compose restart mongodb mysql
```

#### Memory Issues
```bash
# Check memory usage
docker stats --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}"

# Increase Docker memory limit (Docker Desktop)
# Settings → Resources → Memory → Increase limit
```

### Service-Specific Troubleshooting

#### Frontend Issues
- Check if backend is accessible: `curl http://localhost:5000/api/health`
- Verify environment variables in container: `docker-compose exec frontend env`
- Clear browser cache and cookies

#### Backend Issues  
- Check database connectivity
- Verify JWT secret configuration
- Check file permissions for data directory

#### Spring Boot Issues
- Check Java heap memory: `docker-compose logs springboot-mongodb | grep -i memory`
- Verify database connection strings
- Check application properties

## 🔒 Security Considerations

### Production Deployment
1. **Change Default Passwords**: Update all default passwords in `.env`
2. **Use HTTPS**: Configure SSL certificates for Nginx
3. **Firewall Rules**: Restrict access to database ports
4. **Regular Updates**: Keep Docker images updated
5. **Secrets Management**: Use Docker secrets for sensitive data

### Environment Variables
```bash
# Generate secure passwords
openssl rand -base64 32  # For database passwords
openssl rand -hex 64     # For JWT secret
```

## 📈 Performance Optimization

### Container Optimization
```bash
# Multi-stage builds are already implemented
# Use specific versions instead of 'latest'
# Minimize layer count in Dockerfiles
```

### Database Optimization
- **MongoDB**: Enable authentication, use indexes
- **MySQL**: Configure proper buffer sizes
- **Redis**: Set appropriate memory policies

### Nginx Optimization
- Enable gzip compression (already configured)
- Configure proper caching headers
- Use HTTP/2 if available

## 🚢 Production Deployment

### Using Docker Swarm
```bash
# Initialize swarm
docker swarm init

# Deploy stack
docker stack deploy -c docker-compose.yml hostel-stack

# Scale services
docker service scale hostel-stack_frontend=3
```

### Using Kubernetes
```bash
# Convert docker-compose to Kubernetes manifests
kompose convert

# Apply manifests
kubectl apply -f .
```

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Documentation](https://spring.io/guides/gs/spring-boot-docker/)
- [React Docker Documentation](https://create-react-app.dev/docs/deployment/#docker)

## 🆘 Support

If you encounter issues:

1. Check the troubleshooting section above
2. Review container logs: `docker-compose logs [service-name]`
3. Verify your environment configuration
4. Check system resources (memory, disk space)
5. Create an issue in the project repository

## 📝 Changelog

- **v1.0.0**: Initial Docker setup with all four applications
- **v1.1.0**: Added health checks and monitoring
- **v1.2.0**: Improved security and backup features 