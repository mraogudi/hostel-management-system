# Multi-Cloud Hostel Management System Deployment
# This Terraform configuration supports AWS, GCP, and Azure

terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    google = {
      source  = "hashicorp/google"
      version = "~> 5.0"
    }
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.0"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.0"
    }
  }
}

# Variables for cloud provider selection
variable "cloud_provider" {
  description = "Cloud provider to deploy to (aws, gcp, azure)"
  type        = string
  default     = "aws"
  validation {
    condition     = contains(["aws", "gcp", "azure"], var.cloud_provider)
    error_message = "Cloud provider must be one of: aws, gcp, azure"
  }
}

variable "environment" {
  description = "Environment name (dev, staging, prod)"
  type        = string
  default     = "prod"
}

variable "project_name" {
  description = "Project name"
  type        = string
  default     = "hostel-management"
}

variable "region" {
  description = "Region to deploy to"
  type        = string
  default     = ""
}

variable "cluster_name" {
  description = "Kubernetes cluster name"
  type        = string
  default     = ""
}

variable "node_count" {
  description = "Number of nodes in the cluster"
  type        = number
  default     = 3
}

variable "node_instance_type" {
  description = "Instance type for cluster nodes"
  type        = string
  default     = ""
}

variable "enable_auto_scaling" {
  description = "Enable auto scaling for the cluster"
  type        = bool
  default     = true
}

variable "min_node_count" {
  description = "Minimum number of nodes"
  type        = number
  default     = 2
}

variable "max_node_count" {
  description = "Maximum number of nodes"
  type        = number
  default     = 10
}

variable "disk_size" {
  description = "Disk size for nodes in GB"
  type        = number
  default     = 100
}

variable "kubernetes_version" {
  description = "Kubernetes version"
  type        = string
  default     = "1.28"
}

# Local values for cloud-specific configurations
locals {
  # Default regions per cloud provider
  default_regions = {
    aws   = "us-west-2"
    gcp   = "us-central1"
    azure = "East US"
  }
  
  # Default instance types per cloud provider
  default_instance_types = {
    aws   = "t3.medium"
    gcp   = "e2-standard-2"
    azure = "Standard_B2s"
  }
  
  # Resolved values
  region = var.region != "" ? var.region : local.default_regions[var.cloud_provider]
  cluster_name = var.cluster_name != "" ? var.cluster_name : "${var.project_name}-${var.environment}-cluster"
  node_instance_type = var.node_instance_type != "" ? var.node_instance_type : local.default_instance_types[var.cloud_provider]
  
  # Common tags
  common_tags = {
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "terraform"
    Application = "hostel-management-system"
  }
}

# AWS Provider Configuration
provider "aws" {
  count  = var.cloud_provider == "aws" ? 1 : 0
  region = local.region
  
  default_tags {
    tags = local.common_tags
  }
}

# GCP Provider Configuration
provider "google" {
  count   = var.cloud_provider == "gcp" ? 1 : 0
  region  = local.region
  project = var.project_name
}

# Azure Provider Configuration
provider "azurerm" {
  count = var.cloud_provider == "azure" ? 1 : 0
  features {}
}

# AWS EKS Module
module "aws_eks" {
  count  = var.cloud_provider == "aws" ? 1 : 0
  source = "./modules/aws"
  
  project_name        = var.project_name
  environment         = var.environment
  region              = local.region
  cluster_name        = local.cluster_name
  node_count          = var.node_count
  node_instance_type  = local.node_instance_type
  enable_auto_scaling = var.enable_auto_scaling
  min_node_count      = var.min_node_count
  max_node_count      = var.max_node_count
  disk_size           = var.disk_size
  kubernetes_version  = var.kubernetes_version
  
  tags = local.common_tags
}

# GCP GKE Module
module "gcp_gke" {
  count  = var.cloud_provider == "gcp" ? 1 : 0
  source = "./modules/gcp"
  
  project_name        = var.project_name
  environment         = var.environment
  region              = local.region
  cluster_name        = local.cluster_name
  node_count          = var.node_count
  node_instance_type  = local.node_instance_type
  enable_auto_scaling = var.enable_auto_scaling
  min_node_count      = var.min_node_count
  max_node_count      = var.max_node_count
  disk_size           = var.disk_size
  kubernetes_version  = var.kubernetes_version
}

# Azure AKS Module
module "azure_aks" {
  count  = var.cloud_provider == "azure" ? 1 : 0
  source = "./modules/azure"
  
  project_name        = var.project_name
  environment         = var.environment
  region              = local.region
  cluster_name        = local.cluster_name
  node_count          = var.node_count
  node_instance_type  = local.node_instance_type
  enable_auto_scaling = var.enable_auto_scaling
  min_node_count      = var.min_node_count
  max_node_count      = var.max_node_count
  disk_size           = var.disk_size
  kubernetes_version  = var.kubernetes_version
  
  tags = local.common_tags
}

# Kubernetes Provider Configuration
provider "kubernetes" {
  host                   = var.cloud_provider == "aws" ? try(module.aws_eks[0].cluster_endpoint, "") : var.cloud_provider == "gcp" ? try(module.gcp_gke[0].cluster_endpoint, "") : try(module.azure_aks[0].cluster_endpoint, "")
  cluster_ca_certificate = var.cloud_provider == "aws" ? try(base64decode(module.aws_eks[0].cluster_ca_certificate), "") : var.cloud_provider == "gcp" ? try(base64decode(module.gcp_gke[0].cluster_ca_certificate), "") : try(base64decode(module.azure_aks[0].cluster_ca_certificate), "")
  token                  = var.cloud_provider == "aws" ? try(module.aws_eks[0].cluster_token, "") : var.cloud_provider == "gcp" ? try(module.gcp_gke[0].cluster_token, "") : try(module.azure_aks[0].cluster_token, "")
}

# Helm Provider Configuration
provider "helm" {
  kubernetes {
    host                   = var.cloud_provider == "aws" ? try(module.aws_eks[0].cluster_endpoint, "") : var.cloud_provider == "gcp" ? try(module.gcp_gke[0].cluster_endpoint, "") : try(module.azure_aks[0].cluster_endpoint, "")
    cluster_ca_certificate = var.cloud_provider == "aws" ? try(base64decode(module.aws_eks[0].cluster_ca_certificate), "") : var.cloud_provider == "gcp" ? try(base64decode(module.gcp_gke[0].cluster_ca_certificate), "") : try(base64decode(module.azure_aks[0].cluster_ca_certificate), "")
    token                  = var.cloud_provider == "aws" ? try(module.aws_eks[0].cluster_token, "") : var.cloud_provider == "gcp" ? try(module.gcp_gke[0].cluster_token, "") : try(module.azure_aks[0].cluster_token, "")
  }
}

# Application Deployment Module
module "hostel_management_app" {
  source = "./modules/application"
  
  depends_on = [
    module.aws_eks,
    module.gcp_gke,
    module.azure_aks
  ]
  
  project_name     = var.project_name
  environment      = var.environment
  cloud_provider   = var.cloud_provider
  storage_class    = var.cloud_provider == "aws" ? "gp3" : var.cloud_provider == "gcp" ? "standard" : "managed-premium"
}

# Outputs
output "cluster_endpoint" {
  description = "Kubernetes cluster endpoint"
  value       = var.cloud_provider == "aws" ? try(module.aws_eks[0].cluster_endpoint, "") : var.cloud_provider == "gcp" ? try(module.gcp_gke[0].cluster_endpoint, "") : try(module.azure_aks[0].cluster_endpoint, "")
}

output "cluster_name" {
  description = "Kubernetes cluster name"
  value       = local.cluster_name
}

output "cluster_region" {
  description = "Cluster region"
  value       = local.region
}

output "load_balancer_ip" {
  description = "Load balancer IP address"
  value       = module.hostel_management_app.load_balancer_ip
}

output "application_url" {
  description = "Application URL"
  value       = "http://${module.hostel_management_app.load_balancer_ip}"
}

output "kubectl_config_command" {
  description = "Command to configure kubectl"
  value = var.cloud_provider == "aws" ? "aws eks update-kubeconfig --region ${local.region} --name ${local.cluster_name}" : var.cloud_provider == "gcp" ? "gcloud container clusters get-credentials ${local.cluster_name} --region ${local.region}" : "az aks get-credentials --resource-group ${var.project_name}-${var.environment} --name ${local.cluster_name}"
} 