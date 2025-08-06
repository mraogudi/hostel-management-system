# 🚀 Hostel Management System - Complete Deployment Guide

This comprehensive guide covers Docker deployment and CI/CD setup for the Hostel Management System with **4 applications** and **3 databases**.

## 🏗️ System Architecture

### Applications:
1. **React Frontend** (Port 3000) - Modern UI with React 18
2. **Node.js Backend** (Port 5000) - REST API with JWT authentication
3. **Spring Boot + MongoDB** (Port 8080) - Java microservice with NoSQL
4. **Spring Boot + MySQL** (Port 8081) - Java microservice with SQL

### Infrastructure:
- **MongoDB** (Port 27017) - Document database
- **MySQL** (Port 3306) - Relational database  
- **Redis** (Port 6379) - Caching layer
- **Nginx** (Port 80) - Load balancer and reverse proxy

## ⚡ Quick Start (5 minutes)

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (20.10+)
- [Git](https://git-scm.com/)
- 8GB RAM minimum

### Deployment Commands

**Linux/macOS:**
```bash
# Clone and setup
git clone <repository-url>
cd hostel-management-system

# Copy environment template
cp env.example .env

# Deploy (automatic setup)
chmod +x scripts/deploy.sh
./scripts/deploy.sh production
```

**Windows:**
```cmd
# Clone and setup
git clone <repository-url>
cd hostel-management-system

# Copy environment template
copy env.example .env

# Deploy (automatic setup)
scripts\deploy.bat production
```

### 🎯 Access Points
- **Main Application**: http://localhost
- **Frontend**: http://localhost:3000
- **Node.js API**: http://localhost:5000
- **Spring MongoDB**: http://localhost:8080
- **Spring MySQL**: http://localhost:8081

**Default Login:**
- Username: `warden`
- Password: `warden123`

## 🔧 Manual Docker Setup

### 1. Environment Configuration
```bash
cp env.example .env
# Edit .env with your secure passwords
```

### 2. Build and Deploy
```bash
# Build all services
docker-compose build

# Start all services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

### 3. Development Mode
```bash
# Hot reload for development
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
```

## 📁 File Structure

```
hostel-management-system/
├── client/                    # React Frontend
│   ├── Dockerfile
│   ├── nginx.conf
│   └── .dockerignore
├── server/                    # Node.js Backend
│   ├── Dockerfile
│   ├── package.json
│   └── .dockerignore
├── server-spring-boot/        # Spring Boot + MongoDB
│   ├── Dockerfile
│   └── .dockerignore
├── server-spring-boot-mysql/  # Spring Boot + MySQL
│   ├── Dockerfile
│   └── .dockerignore
├── nginx/                     # Load Balancer Config
│   └── nginx.conf
├── scripts/                   # Deployment Scripts
│   ├── deploy.sh             # Linux/macOS deployment
│   ├── deploy.bat            # Windows deployment
│   ├── backup.sh             # Database backup
│   ├── mongo-init.js         # MongoDB initialization
│   └── mysql-init.sql        # MySQL initialization
├── .github/workflows/         # CI/CD Pipeline
│   └── ci-cd.yml             # GitHub Actions
├── docker-compose.yml         # Production setup
├── docker-compose.dev.yml     # Development overrides
├── env.example               # Environment template
└── README-DOCKER.md          # Detailed Docker guide
```

## 🔄 CI/CD Pipeline

The GitHub Actions pipeline includes:

### ✅ Automated Testing
- **Frontend**: React tests with coverage
- **Backend**: Node.js tests and linting
- **Spring Boot MongoDB**: Maven tests with MongoDB
- **Spring Boot MySQL**: Maven tests with MySQL

### 🔒 Security Scanning
- **Trivy vulnerability scanner**
- **Dependency security checks**
- **SARIF results to GitHub Security**

### 🐳 Docker Build & Push
- **Multi-architecture builds** (AMD64, ARM64)
- **GitHub Container Registry**
- **Automated tagging** (latest, branch, SHA)
- **Build caching** for faster builds

### 🚀 Deployment Stages
1. **Staging Environment** - Automatic on main branch
2. **Integration Tests** - Automated testing
3. **Production Deployment** - Manual approval required
4. **Health Checks** - Automated verification
5. **Rollback** - Automatic on failure

### 📢 Notifications
- **Slack integration** for deployment status
- **GitHub status checks**
- **Email notifications** (configurable)

## 💾 Backup & Restore

### Automated Backup
```bash
# Linux/macOS
./scripts/backup.sh

# Windows  
scripts\backup.bat
```

### Manual Database Backup
```bash
# MySQL backup
docker-compose exec mysql mysqldump --all-databases -u root -p > backup.sql

# MongoDB backup
docker-compose exec mongodb mongodump --out /tmp/backup
docker cp $(docker-compose ps -q mongodb):/tmp/backup ./mongo-backup
```

## 🔍 Monitoring & Health Checks

### Health Endpoints
- Frontend: `http://localhost:3000`
- Node.js: `http://localhost:5000/api/health`
- Spring MongoDB: `http://localhost:8080/actuator/health`
- Spring MySQL: `http://localhost:8081/actuator/health`
- Load Balancer: `http://localhost/health`

### Monitoring Commands
```bash
# Service status
docker-compose ps

# Resource usage
docker stats

# Live logs
docker-compose logs -f [service-name]
```

## 🛠️ Common Commands

### Service Management
```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# Restart specific service
docker-compose restart frontend

# Scale services
docker-compose up -d --scale frontend=3

# View service logs
docker-compose logs -f backend
```

### Database Access
```bash
# MongoDB shell
docker-compose exec mongodb mongosh -u admin -p admin123

# MySQL shell  
docker-compose exec mysql mysql -u root -p

# Redis CLI
docker-compose exec redis redis-cli
```

### Troubleshooting
```bash
# Check container health
docker-compose ps

# View detailed logs
docker-compose logs --tail=100 [service-name]

# Rebuild problematic service
docker-compose build --no-cache [service-name]
docker-compose up -d [service-name]

# Clean Docker system
docker system prune -f
```

## 🔒 Security Features

### Built-in Security
- **JWT Authentication** with secure secrets
- **Rate limiting** via Nginx
- **CORS protection** configured
- **SQL injection prevention** with prepared statements
- **XSS protection** headers
- **Non-root containers** for security
- **Network isolation** between services

### Production Security Checklist
- [ ] Change all default passwords in `.env`
- [ ] Generate strong JWT secrets (64+ characters)
- [ ] Configure SSL/TLS certificates
- [ ] Set up firewall rules
- [ ] Enable database authentication
- [ ] Regular security updates
- [ ] Monitor access logs

## 🎛️ Environment Variables

Key environment variables in `.env`:

```env
# Database Security
MYSQL_ROOT_PASSWORD=your_secure_password
MYSQL_USER=hostel_user  
MYSQL_PASSWORD=your_secure_password
MONGO_ROOT_USERNAME=admin
MONGO_ROOT_PASSWORD=your_secure_password

# Application Security
JWT_SECRET=your_64_character_secret_key
NODE_ENV=production

# Application URLs
REACT_APP_API_URL=http://localhost:5000/api
REACT_APP_SPRING_API_URL=http://localhost:8080/api

# Docker Configuration
COMPOSE_PROJECT_NAME=hostel-management
```

## 📊 Performance Optimization

### Container Optimization
- **Multi-stage Docker builds** for smaller images
- **Alpine Linux** base images for security and size
- **Health checks** for automatic recovery
- **Resource limits** to prevent overconsumption
- **Build caching** for faster deployments

### Database Optimization  
- **Connection pooling** in Spring Boot
- **Database indexes** for faster queries
- **Redis caching** for session management
- **Optimized queries** with proper joins

### Nginx Optimization
- **Gzip compression** for faster loading
- **Static file caching** with proper headers
- **Load balancing** across multiple instances
- **Rate limiting** for DDoS protection

## 🚨 Troubleshooting Guide

### Common Issues

#### Port Conflicts
```bash
# Windows
netstat -ano | findstr :3000

# Linux/macOS  
netstat -tulpn | grep :3000
```

#### Memory Issues
- Increase Docker Desktop memory limit (Settings → Resources)
- Check container memory usage: `docker stats`
- Clean unused containers: `docker system prune -f`

#### Database Connection Issues
- Verify database containers are running: `docker-compose ps`
- Check database logs: `docker-compose logs mongodb mysql`
- Restart database services: `docker-compose restart mongodb mysql`

#### Build Issues
- Clear Docker cache: `docker-compose build --no-cache`
- Check Dockerfile syntax
- Verify all required files exist

## 📚 Documentation

- **[Complete Docker Guide](README-DOCKER.md)** - Detailed Docker instructions
- **[API Documentation](api-specifications.yaml)** - OpenAPI specifications
- **[Application READMEs](README.md)** - Individual application guides

## 🎯 Production Deployment

### Cloud Deployment Options
1. **AWS ECS/Fargate** - Fully managed containers
2. **Google Cloud Run** - Serverless containers
3. **Azure Container Instances** - Simple container hosting
4. **Kubernetes** - Full orchestration (use `kompose convert`)
5. **Docker Swarm** - Simple cluster management

### Deployment Commands
```bash
# Docker Swarm
docker swarm init
docker stack deploy -c docker-compose.yml hostel-stack

# Kubernetes (after kompose convert)
kubectl apply -f .
```

## 💡 Best Practices

1. **Environment Management**: Use different `.env` files for each environment
2. **Secret Management**: Never commit secrets to version control
3. **Regular Backups**: Automate daily database backups
4. **Monitoring**: Set up log aggregation and metrics
5. **Updates**: Keep Docker images and dependencies updated
6. **Testing**: Run automated tests before deployment
7. **Documentation**: Keep deployment docs updated

## 🆘 Support

Need help? Check these resources:

1. **[Troubleshooting Section](#-troubleshooting-guide)** above
2. **Container Logs**: `docker-compose logs [service-name]`
3. **Health Checks**: Visit the health endpoints listed above
4. **GitHub Issues**: Create an issue in the repository
5. **Docker Documentation**: [docs.docker.com](https://docs.docker.com/)

## 🏆 Features Achieved

✅ **Complete Containerization** - All 4 apps dockerized  
✅ **Multi-Database Support** - MongoDB, MySQL, Redis  
✅ **Load Balancing** - Nginx reverse proxy  
✅ **Health Monitoring** - Automated health checks  
✅ **Automated CI/CD** - GitHub Actions pipeline  
✅ **Security Hardening** - Non-root containers, secrets  
✅ **Backup & Recovery** - Automated database backups  
✅ **Development Support** - Hot reload in dev mode  
✅ **Cross-Platform** - Linux, macOS, Windows support  
✅ **Production Ready** - Scalable, monitored, secure  

---

**🎉 Congratulations!** You now have a fully automated, production-ready hostel management system with comprehensive Docker deployment and CI/CD pipeline! 