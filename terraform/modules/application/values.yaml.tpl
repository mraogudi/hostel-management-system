# Helm values template for Hostel Management System
# This template is populated by Terraform with cloud-specific values

global:
  projectName: "${project_name}"
  environment: "${environment}"
  cloudProvider: "${cloud_provider}"
  storageClass: "${storage_class}"
  
  # Image registry configuration
  imageRegistry: "ghcr.io"
  imageTag: "latest"
  
  # Pull policy
  imagePullPolicy: "Always"

# Frontend configuration
frontend:
  enabled: true
  replicaCount: 3
  
  image:
    repository: ghcr.io/hostel-management-system-frontend
    tag: latest
    pullPolicy: Always
  
  service:
    type: ClusterIP
    port: 80
  
  resources:
    requests:
      memory: "64Mi"
      cpu: "50m"
    limits:
      memory: "256Mi"
      cpu: "500m"
  
  autoscaling:
    enabled: true
    minReplicas: 2
    maxReplicas: 10
    targetCPUUtilizationPercentage: 70
    targetMemoryUtilizationPercentage: 80

# Backend configuration  
backend:
  enabled: true
  replicaCount: 2
  
  image:
    repository: ghcr.io/hostel-management-system-backend
    tag: latest
    pullPolicy: Always
  
  service:
    type: ClusterIP
    port: 5000
  
  resources:
    requests:
      memory: "256Mi"
      cpu: "250m"
    limits:
      memory: "1Gi"
      cpu: "1000m"
  
  persistence:
    enabled: true
    storageClass: "${storage_class}"
    size: 2Gi
  
  autoscaling:
    enabled: true
    minReplicas: 2
    maxReplicas: 10
    targetCPUUtilizationPercentage: 70
    targetMemoryUtilizationPercentage: 80

# Spring Boot MongoDB configuration
springbootMongodb:
  enabled: true
  replicaCount: 2
  
  image:
    repository: ghcr.io/hostel-management-system-springboot-mongodb
    tag: latest
    pullPolicy: Always
  
  service:
    type: ClusterIP
    port: 8080
  
  resources:
    requests:
      memory: "512Mi"
      cpu: "500m"
    limits:
      memory: "2Gi"
      cpu: "2000m"
  
  autoscaling:
    enabled: true
    minReplicas: 2
    maxReplicas: 10
    targetCPUUtilizationPercentage: 70
    targetMemoryUtilizationPercentage: 80

# Spring Boot MySQL configuration
springbootMysql:
  enabled: true
  replicaCount: 2
  
  image:
    repository: ghcr.io/hostel-management-system-springboot-mysql
    tag: latest
    pullPolicy: Always
  
  service:
    type: ClusterIP
    port: 8080
  
  resources:
    requests:
      memory: "512Mi"
      cpu: "500m"
    limits:
      memory: "2Gi"
      cpu: "2000m"
  
  autoscaling:
    enabled: true
    minReplicas: 2
    maxReplicas: 10
    targetCPUUtilizationPercentage: 70
    targetMemoryUtilizationPercentage: 80

# MongoDB configuration
mongodb:
  enabled: true
  replicaCount: 1
  
  image:
    repository: mongo
    tag: "7.0"
    pullPolicy: IfNotPresent
  
  service:
    type: ClusterIP
    port: 27017
  
  persistence:
    enabled: true
    storageClass: "${storage_class}"
    size: 10Gi
  
  resources:
    requests:
      memory: "512Mi"
      cpu: "250m"
    limits:
      memory: "2Gi"
      cpu: "1000m"

# MySQL configuration
mysql:
  enabled: true
  replicaCount: 1
  
  image:
    repository: mysql
    tag: "8.0"
    pullPolicy: IfNotPresent
  
  service:
    type: ClusterIP
    port: 3306
  
  persistence:
    enabled: true
    storageClass: "${storage_class}"
    size: 10Gi
  
  resources:
    requests:
      memory: "512Mi"
      cpu: "250m"
    limits:
      memory: "2Gi"
      cpu: "1000m"

# Redis configuration
redis:
  enabled: true
  replicaCount: 1
  
  image:
    repository: redis
    tag: "7.2-alpine"
    pullPolicy: IfNotPresent
  
  service:
    type: ClusterIP
    port: 6379
  
  persistence:
    enabled: true
    storageClass: "${storage_class}"
    size: 5Gi
  
  resources:
    requests:
      memory: "128Mi"
      cpu: "100m"
    limits:
      memory: "512Mi"
      cpu: "500m"

# Nginx Load Balancer configuration
nginx:
  enabled: true
  replicaCount: 2
  
  image:
    repository: nginx
    tag: "alpine"
    pullPolicy: IfNotPresent
  
  service:
    type: LoadBalancer
    port: 80
    httpsPort: 443
    annotations:
      %{~ if cloud_provider == "aws" }
      service.beta.kubernetes.io/aws-load-balancer-type: "nlb"
      service.beta.kubernetes.io/aws-load-balancer-scheme: "internet-facing"
      %{~ endif }
      %{~ if cloud_provider == "gcp" }
      cloud.google.com/load-balancer-type: "External"
      %{~ endif }
      %{~ if cloud_provider == "azure" }
      service.beta.kubernetes.io/azure-load-balancer-resource-group: "${project_name}-${environment}"
      %{~ endif }
  
  resources:
    requests:
      memory: "64Mi"
      cpu: "50m"
    limits:
      memory: "256Mi"
      cpu: "500m"

# Ingress configuration (alternative to nginx load balancer)
ingress:
  enabled: false  # Enable if you prefer ingress over LoadBalancer service
  className: "%{~ if cloud_provider == "aws" }alb%{~ endif }%{~ if cloud_provider == "gcp" }gce%{~ endif }%{~ if cloud_provider == "azure" }azure/application-gateway%{~ endif }"
  host: "hostel-management.local"
  annotations:
    %{~ if cloud_provider == "aws" }
    alb.ingress.kubernetes.io/scheme: "internet-facing"
    alb.ingress.kubernetes.io/target-type: "ip"
    %{~ endif }

# Security configuration
security:
  networkPolicies:
    enabled: true
  
  podSecurityStandards:
    enabled: true
    level: "restricted"

# Monitoring configuration
monitoring:
  enabled: true
  prometheus:
    enabled: true
  grafana:
    enabled: true
  alertmanager:
    enabled: true

# Backup configuration
backup:
  enabled: true
  schedule: "0 2 * * *"  # Daily at 2 AM
  retention: "7d"
  
  s3:
    enabled: %{~ if cloud_provider == "aws" }true%{~ else }false%{~ endif }
    bucket: "${project_name}-${environment}-backups"
  
  gcs:
    enabled: %{~ if cloud_provider == "gcp" }true%{~ else }false%{~ endif }
    bucket: "${project_name}-${environment}-backups"
  
  azure:
    enabled: %{~ if cloud_provider == "azure" }true%{~ else }false%{~ endif }
    storageAccount: "${project_name}${environment}backups" 