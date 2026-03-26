#!/bin/bash

# Azure Container Instances Deployment Script
# Backend Torneos

set -e

# Variables
RESOURCE_GROUP=${RESOURCE_GROUP:-torneos-rg}
LOCATION=${LOCATION:-eastus}
CONTAINER_NAME=${CONTAINER_NAME:-torneos-backend}
IMAGE_NAME=${IMAGE_NAME:-{DOCKER_USERNAME}/torneos-backend:latest}

echo "=== Azure Container Instances Deployment ==="
echo "Resource Group: $RESOURCE_GROUP"
echo "Location: $LOCATION"
echo "Container: $CONTAINER_NAME"
echo ""

# 1. Create resource group if not exists
echo "1. Ensuring resource group exists..."
az group create \
  --name $RESOURCE_GROUP \
  --location $LOCATION

# 2. Deploy container
echo "2. Deploying container..."
az deployment group create \
  --resource-group $RESOURCE_GROUP \
  --template-file container-instance.json \
  --parameters \
    containerName=$CONTAINER_NAME \
    imageName=$IMAGE_NAME \
    dbHost=$DB_HOST \
    dbPassword=$DB_PASSWORD \
    jwtSecret=$JWT_SECRET

# 3. Get container info
echo "3. Getting container information..."
az container show \
  --resource-group $RESOURCE_GROUP \
  --name $CONTAINER_NAME \
  --query "{FQDN:ipAddress.fqdn,IP:ipAddress.ip,State:instanceView.state}" \
  --output table

echo ""
echo "✓ Deployment completed successfully!"
echo "Access your application at: http://$(az container show --resource-group $RESOURCE_GROUP --name $CONTAINER_NAME --query ipAddress.fqdn -o tsv):8081"
