#!/bin/bash

# AWS ECS Deployment Script
# Backend Torneos

set -e

# Variables
REGION=${AWS_REGION:-us-east-1}
CLUSTER_NAME=${CLUSTER_NAME:-torneos-cluster}
SERVICE_NAME=${SERVICE_NAME:-torneos-backend-service}
TASK_FAMILY=torneos-backend
IMAGE_TAG=${IMAGE_TAG:-latest}

echo "=== AWS ECS Deployment ==="
echo "Region: $REGION"
echo "Cluster: $CLUSTER_NAME"
echo "Service: $SERVICE_NAME"
echo ""

# 1. Register task definition
echo "1. Registering task definition..."
aws ecs register-task-definition \
  --cli-input-json file://task-definition.json \
  --region $REGION

# 2. Update service
echo "2. Updating ECS service..."
aws ecs update-service \
  --cluster $CLUSTER_NAME \
  --service $SERVICE_NAME \
  --task-definition $TASK_FAMILY \
  --force-new-deployment \
  --region $REGION

# 3. Wait for deployment
echo "3. Waiting for deployment to complete..."
aws ecs wait services-stable \
  --cluster $CLUSTER_NAME \
  --services $SERVICE_NAME \
  --region $REGION

echo ""
echo "✓ Deployment completed successfully!"
