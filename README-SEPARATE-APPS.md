# 🎯 Hostel Management System - Separate Application Deployments

## 📋 **Overview**

This guide shows how to deploy and manage each application separately in Kubernetes. Each application has its own set of manifests for maximum flexibility and independent scaling.

## 🏗️ **Separated Architecture**

```
k8s/
├── apps/
│   ├── frontend/               # React Frontend
│   │   ├── deployment.yaml    # Frontend deployment
│   │   ├── service.yaml       # Frontend service
│   │   └── hpa.yaml           # Auto-scaling
│   ├── backend/                # Node.js Backend
│   │   ├── deployment.yaml    # Backend deployment
│   │   ├── service.yaml       # Backend service
│   │   ├── hpa.yaml           # Auto-scaling
│   │   └── pvc.yaml           # Storage
│   ├── springboot-mongodb/     # Spring Boot + MongoDB
│   │   ├── deployment.yaml    # Spring Boot deployment
│   │   ├── service.yaml       # Spring Boot service
│   │   └── hpa.yaml           # Auto-scaling
│   └── springboot-mysql/       # Spring Boot + MySQL
│       ├── deployment.yaml    # Spring Boot deployment
│       ├── service.yaml       # Spring Boot service
│       └── hpa.yaml           # Auto-scaling
├── overlays/
│   ├── development/           # Dev environment configs
│   └── production/            # Prod environment configs
└── [infrastructure files]     # Shared infrastructure
```

## ⚡ **Quick Deployment Commands**

### **Deploy Individual Applications**

**Deploy React Frontend:**
```bash
./scripts/deploy-app.sh frontend
```

**Deploy Node.js Backend:**
```bash
./scripts/deploy-app.sh backend
```

**Deploy Spring Boot + MongoDB:**
```bash
./scripts/deploy-app.sh springboot-mongodb
```

**Deploy Spring Boot + MySQL:**
```bash
./scripts/deploy-app.sh springboot-mysql
```

**Deploy All Applications:**
```bash
./scripts/deploy-app.sh all
```

### **Environment-Specific Deployments**

**Development Environment:**
```bash
# Using Kustomize overlays
kubectl apply -k k8s/overlays/development/

# Using individual scripts
./scripts/deploy-app.sh frontend hostel-management-dev
```

**Production Environment:**
```bash
# Using Kustomize overlays
kubectl apply -k k8s/overlays/production/

# Using individual scripts  
./scripts/deploy-app.sh all hostel-management
```

## 🔧 **Application Management**

### **Application Operations**

**Check Application Status:**
```bash
./scripts/deploy-app.sh frontend hostel-management status
./scripts/deploy-app.sh backend hostel-management status
./scripts/deploy-app.sh springboot-mongodb hostel-management status
./scripts/deploy-app.sh springboot-mysql hostel-management status
```

**Restart Applications:**
```bash
./scripts/deploy-app.sh frontend hostel-management restart
./scripts/deploy-app.sh backend hostel-management restart
./scripts/deploy-app.sh all hostel-management restart
```

**View Application Logs:**
```bash
./scripts/deploy-app.sh frontend hostel-management logs
./scripts/deploy-app.sh backend hostel-management logs
./scripts/deploy-app.sh springboot-mongodb hostel-management logs
./scripts/deploy-app.sh springboot-mysql hostel-management logs
```

**Delete Applications:**
```bash
./scripts/deploy-app.sh frontend hostel-management delete
./scripts/deploy-app.sh backend hostel-management delete
./scripts/deploy-app.sh all hostel-management delete
```

### **Kubectl Direct Commands**

**Deploy Individual Applications:**
```bash
# Frontend
kubectl apply -f k8s/apps/frontend/

# Backend
kubectl apply -f k8s/apps/backend/

# Spring Boot MongoDB
kubectl apply -f k8s/apps/springboot-mongodb/

# Spring Boot MySQL
kubectl apply -f k8s/apps/springboot-mysql/
```

**Scale Individual Applications:**
```bash
kubectl scale deployment frontend --replicas=5 -n hostel-management
kubectl scale deployment backend --replicas=3 -n hostel-management
kubectl scale deployment springboot-mongodb --replicas=4 -n hostel-management
kubectl scale deployment springboot-mysql --replicas=4 -n hostel-management
```

## 🌍 **Environment-Specific Configurations**

### **Development Environment**

**Features:**
- Reduced resource limits
- Single replica per service
- Debug logging enabled
- Development image tags

**Deploy:**
```bash
kubectl apply -k k8s/overlays/development/
```

**Resource Limits (Development):**
- **Frontend**: 32Mi RAM, 25m CPU
- **Backend**: 128Mi RAM, 100m CPU  
- **Spring Boot**: 256Mi RAM, 200m CPU

### **Production Environment**

**Features:**
- Optimized resource limits
- High availability (3-5 replicas)
- Production logging
- Hardened security context
- Versioned image tags

**Deploy:**
```bash
kubectl apply -k k8s/overlays/production/
```

**Resource Limits (Production):**
- **Frontend**: 512Mi RAM, 1000m CPU
- **Backend**: 2Gi RAM, 2000m CPU
- **Spring Boot**: 4Gi RAM, 4000m CPU

## 📊 **Application Details**

### **🌐 Frontend (React)**

**Components:**
- **Deployment**: React app with Nginx
- **Service**: ClusterIP on port 80
- **HPA**: 2-10 replicas based on CPU/memory
- **Security**: Read-only filesystem, non-root user

**Configuration:**
- Environment variables from ConfigMap
- Health checks on root path
- Prometheus metrics enabled

### **⚙️ Backend (Node.js)**

**Components:**
- **Deployment**: Express.js API server
- **Service**: ClusterIP on port 5000
- **HPA**: 2-10 replicas based on CPU/memory
- **PVC**: 2Gi persistent storage for data
- **Security**: Non-root user, limited capabilities

**Configuration:**
- JWT secret from Kubernetes secrets
- Health endpoint: `/api/health`
- Persistent data storage

### **☕ Spring Boot + MongoDB**

**Components:**
- **Deployment**: Java application
- **Service**: ClusterIP on port 8080 (app) + 8081 (management)
- **HPA**: 2-10 replicas based on CPU/memory
- **Security**: Non-root user, security context

**Configuration:**
- MongoDB connection via service discovery
- Actuator endpoints for monitoring
- JVM optimization for containers

### **☕ Spring Boot + MySQL**

**Components:**
- **Deployment**: Java application
- **Service**: ClusterIP on port 8080 (app) + 8081 (management)
- **HPA**: 2-10 replicas based on CPU/memory
- **Security**: Non-root user, security context

**Configuration:**
- MySQL connection via service discovery
- JPA/Hibernate auto-configuration
- Database migrations handled automatically

## 🔄 **Rolling Updates**

### **Update Individual Applications**

**Update Frontend:**
```bash
kubectl set image deployment/frontend frontend=ghcr.io/hostel-management-system-frontend:v2.0.0 -n hostel-management
kubectl rollout status deployment/frontend -n hostel-management
```

**Update Backend:**
```bash
kubectl set image deployment/backend backend=ghcr.io/hostel-management-system-backend:v2.0.0 -n hostel-management
kubectl rollout status deployment/backend -n hostel-management
```

**Update Spring Boot Applications:**
```bash
kubectl set image deployment/springboot-mongodb springboot-mongodb=ghcr.io/hostel-management-system-springboot-mongodb:v2.0.0 -n hostel-management
kubectl set image deployment/springboot-mysql springboot-mysql=ghcr.io/hostel-management-system-springboot-mysql:v2.0.0 -n hostel-management
```

### **Rollback Applications**

**Rollback to Previous Version:**
```bash
kubectl rollout undo deployment/frontend -n hostel-management
kubectl rollout undo deployment/backend -n hostel-management
kubectl rollout undo deployment/springboot-mongodb -n hostel-management
kubectl rollout undo deployment/springboot-mysql -n hostel-management
```

**Check Rollout History:**
```bash
kubectl rollout history deployment/frontend -n hostel-management
kubectl rollout history deployment/backend -n hostel-management
```

## 📈 **Monitoring & Observability**

### **Application Health Checks**

**Frontend Health:**
```bash
kubectl get pods -l app.kubernetes.io/component=frontend -n hostel-management
kubectl port-forward service/frontend-service 8080:80 -n hostel-management
# Access: http://localhost:8080
```

**Backend Health:**
```bash
kubectl get pods -l app.kubernetes.io/component=backend -n hostel-management
kubectl port-forward service/backend-service 8080:5000 -n hostel-management
# Access: http://localhost:8080/api/health
```

**Spring Boot Health:**
```bash
kubectl get pods -l app.kubernetes.io/component=springboot-mongodb -n hostel-management
kubectl port-forward service/springboot-mongodb-service 8081:8081 -n hostel-management
# Access: http://localhost:8081/actuator/health
```

### **Metrics and Monitoring**

**Prometheus Metrics:**
- All applications expose metrics on `/metrics` or `/actuator/prometheus`
- HPA uses CPU and memory metrics for auto-scaling
- Custom business metrics available

**Grafana Dashboards:**
- Application-specific dashboards
- Performance monitoring
- Error rate tracking

## 🚀 **Advanced Deployment Patterns**

### **Blue-Green Deployment**

**Deploy New Version (Green):**
```bash
# Update deployment with new image
kubectl patch deployment frontend -p '{"spec":{"template":{"metadata":{"labels":{"version":"green"}}}}}' -n hostel-management
kubectl set image deployment/frontend frontend=ghcr.io/hostel-management-system-frontend:v2.0.0 -n hostel-management

# Switch service to green
kubectl patch service frontend-service -p '{"spec":{"selector":{"version":"green"}}}' -n hostel-management
```

### **Canary Deployment**

**Deploy Canary Version:**
```bash
# Create canary deployment
kubectl apply -f - <<EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend-canary
  namespace: hostel-management
spec:
  replicas: 1
  selector:
    matchLabels:
      app.kubernetes.io/component: frontend
      version: canary
  template:
    metadata:
      labels:
        app.kubernetes.io/component: frontend
        version: canary
    spec:
      containers:
      - name: frontend
        image: ghcr.io/hostel-management-system-frontend:v2.0.0-canary
        # ... rest of container spec
EOF

# Monitor canary performance
kubectl get pods -l version=canary -n hostel-management
```

## 💾 **Backup and Recovery**

### **Application-Specific Backups**

**Backend Data Backup:**
```bash
# Create backup of backend data
kubectl exec deployment/backend -n hostel-management -- tar czf /tmp/backend-backup.tar.gz /app/data
kubectl cp hostel-management/$(kubectl get pod -l app.kubernetes.io/component=backend -o jsonpath='{.items[0].metadata.name}'):/tmp/backend-backup.tar.gz ./backend-backup.tar.gz
```

**Database Backups:**
```bash
# MongoDB backup
kubectl exec deployment/mongodb -n hostel-management -- mongodump --out /tmp/backup
kubectl cp hostel-management/$(kubectl get pod -l app.kubernetes.io/component=mongodb -o jsonpath='{.items[0].metadata.name}'):/tmp/backup ./mongodb-backup

# MySQL backup
kubectl exec deployment/mysql -n hostel-management -- mysqldump --all-databases > mysql-backup.sql
```

## 🛠️ **Troubleshooting**

### **Common Issues**

**Application Not Starting:**
```bash
# Check pod status
kubectl get pods -l app.kubernetes.io/component=frontend -n hostel-management
kubectl describe pod <pod-name> -n hostel-management
kubectl logs <pod-name> -n hostel-management
```

**Service Not Accessible:**
```bash
# Check service and endpoints
kubectl get service frontend-service -n hostel-management
kubectl get endpoints frontend-service -n hostel-management
kubectl describe service frontend-service -n hostel-management
```

**Database Connection Issues:**
```bash
# Test database connectivity
kubectl exec -it deployment/backend -n hostel-management -- curl -f http://mongodb-service:27017
kubectl exec -it deployment/springboot-mysql -n hostel-management -- curl -f http://mysql-service:3306
```

**Resource Constraints:**
```bash
# Check resource usage
kubectl top pods -n hostel-management
kubectl describe hpa -n hostel-management

# Check node resources
kubectl top nodes
kubectl describe nodes
```

## 🎯 **Benefits of Separate Applications**

### **✅ Advantages**

1. **🔧 Independent Scaling** - Scale each service based on demand
2. **🚀 Independent Deployments** - Deploy updates without affecting other services
3. **🔍 Better Debugging** - Easier to isolate and troubleshoot issues
4. **💰 Cost Optimization** - Right-size resources for each application
5. **👥 Team Ownership** - Different teams can own different services
6. **🔄 Flexible Updates** - Update applications at different cadences
7. **🛡️ Improved Security** - Isolate security contexts per application
8. **📊 Granular Monitoring** - Monitor each service independently

### **📊 Management Benefits**

- **Easier Maintenance**: Update one service without affecting others
- **Better Resource Utilization**: Each app gets exactly what it needs
- **Improved Reliability**: Service failures don't cascade
- **Enhanced Observability**: Clear metrics and logs per service
- **Flexible Deployment**: Choose deployment strategy per application

## 📚 **Quick Reference**

### **Deployment Script Usage**
```bash
# Syntax
./scripts/deploy-app.sh <app_name> [namespace] [action]

# Applications: all, frontend, backend, springboot-mongodb, springboot-mysql
# Actions: apply, delete, restart, status, logs

# Examples
./scripts/deploy-app.sh frontend                           # Deploy frontend
./scripts/deploy-app.sh backend hostel-management delete   # Delete backend  
./scripts/deploy-app.sh all hostel-management restart      # Restart all
./scripts/deploy-app.sh frontend hostel-management status  # Show status
./scripts/deploy-app.sh backend hostel-management logs     # Show logs
```

### **Kustomize Commands**
```bash
# Deploy with environment-specific configs
kubectl apply -k k8s/overlays/development/    # Development
kubectl apply -k k8s/overlays/production/     # Production
kubectl apply -k k8s/                         # Base configuration

# Preview changes
kubectl diff -k k8s/overlays/production/
```

---

## 🎉 **Summary**

You now have **complete flexibility** to deploy and manage each application independently! This modular approach provides:

- **🎯 Precise Control** over each service
- **🔄 Independent Lifecycle Management**
- **📈 Optimized Resource Usage**  
- **🛡️ Enhanced Security and Isolation**
- **👥 Team-Based Service Ownership**

Each application can be developed, deployed, scaled, and monitored independently while still working together as a cohesive system! 🚀 