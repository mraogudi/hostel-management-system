# Application Deployment Module - Cloud Independent
# This module deploys the Hostel Management System to any Kubernetes cluster

variable "project_name" {
  description = "Project name"
  type        = string
}

variable "environment" {
  description = "Environment name"
  type        = string
}

variable "cloud_provider" {
  description = "Cloud provider (aws, gcp, azure)"
  type        = string
}

variable "storage_class" {
  description = "Storage class name for the cloud provider"
  type        = string
}

# Helm Release for the Hostel Management System
resource "helm_release" "hostel_management" {
  name       = "hostel-management"
  chart      = "${path.module}/../../../helm/hostel-management"
  namespace  = "hostel-management"
  create_namespace = true

  values = [
    templatefile("${path.module}/values.yaml.tpl", {
      project_name    = var.project_name
      environment     = var.environment
      cloud_provider  = var.cloud_provider
      storage_class   = var.storage_class
    })
  ]

  depends_on = [
    kubernetes_namespace.hostel_management
  ]
}

# Create namespace using Kubernetes provider
resource "kubernetes_namespace" "hostel_management" {
  metadata {
    name = "hostel-management"
    labels = {
      "app.kubernetes.io/name"       = "hostel-management-system"
      "app.kubernetes.io/version"    = "1.0.0"
      "app.kubernetes.io/managed-by" = "terraform"
      "environment"                  = var.environment
      "cloud-provider"               = var.cloud_provider
    }
  }
}

# Create secrets using Kubernetes provider
resource "kubernetes_secret" "hostel_secrets" {
  metadata {
    name      = "hostel-secrets"
    namespace = kubernetes_namespace.hostel_management.metadata[0].name
    labels = {
      "app.kubernetes.io/name"      = "hostel-management-system"
      "app.kubernetes.io/component" = "secrets"
    }
  }

  type = "Opaque"

  data = {
    # Database passwords (base64 encoded)
    mysql-root-password = base64encode("root123")
    mysql-password      = base64encode("hostel_password")
    mongo-root-username = base64encode("admin")
    mongo-root-password = base64encode("admin123")
    jwt-secret         = base64encode("hostel_management_secret_key_2024_very_secure")
  }
}

# Create configmap using Kubernetes provider
resource "kubernetes_config_map" "hostel_config" {
  metadata {
    name      = "hostel-config"
    namespace = kubernetes_namespace.hostel_management.metadata[0].name
    labels = {
      "app.kubernetes.io/name"      = "hostel-management-system"
      "app.kubernetes.io/component" = "config"
    }
  }

  data = {
    NODE_ENV                 = "production"
    PORT                     = "5000"
    MONGO_INITDB_DATABASE    = "hostel_management"
    MYSQL_DATABASE           = "hostel_management"
    MYSQL_USER               = "hostel_user"
    REACT_APP_API_URL        = "http://localhost:5000/api"
    REACT_APP_SPRING_API_URL = "http://localhost:8080/api"
    SPRING_PROFILES_ACTIVE   = "kubernetes"
    REDIS_HOST               = "redis-service"
    REDIS_PORT               = "6379"
    MONGO_HOST               = "mongodb-service"
    MONGO_PORT               = "27017"
    MYSQL_HOST               = "mysql-service"
    MYSQL_PORT               = "3306"
  }
}

# Get load balancer IP
data "kubernetes_service" "nginx_lb" {
  metadata {
    name      = "nginx-lb-service"
    namespace = kubernetes_namespace.hostel_management.metadata[0].name
  }

  depends_on = [helm_release.hostel_management]
}

# Output the load balancer IP
output "load_balancer_ip" {
  description = "Load balancer IP address"
  value = try(
    data.kubernetes_service.nginx_lb.status[0].load_balancer[0].ingress[0].ip,
    data.kubernetes_service.nginx_lb.status[0].load_balancer[0].ingress[0].hostname,
    "pending"
  )
}

output "namespace" {
  description = "Application namespace"
  value       = kubernetes_namespace.hostel_management.metadata[0].name
}

output "helm_release_status" {
  description = "Helm release status"
  value       = helm_release.hostel_management.status
} 