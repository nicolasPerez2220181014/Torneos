.PHONY: build test docker-build docker-push security-scan clean

# Variables
IMAGE_NAME=torneos-backend
VERSION=$(shell date +%Y%m%d)-$(shell git rev-parse --short HEAD)
DOCKER_USERNAME?=your-username

# Build
build:
	@echo "Building application..."
	mvn clean compile

# Test
test:
	@echo "Running tests..."
	mvn test

# Package
package:
	@echo "Packaging application..."
	mvn package -DskipTests

# Docker build
docker-build:
	@echo "Building Docker image..."
	docker build -t $(DOCKER_USERNAME)/$(IMAGE_NAME):$(VERSION) .
	docker tag $(DOCKER_USERNAME)/$(IMAGE_NAME):$(VERSION) $(DOCKER_USERNAME)/$(IMAGE_NAME):latest

# Docker push
docker-push: docker-build
	@echo "Pushing Docker image..."
	docker push $(DOCKER_USERNAME)/$(IMAGE_NAME):$(VERSION)
	docker push $(DOCKER_USERNAME)/$(IMAGE_NAME):latest

# Security scan
security-scan:
	@echo "Running security scan..."
	docker run --rm -v $(PWD):/app aquasec/trivy:latest fs /app

# Full pipeline
pipeline: build test package docker-build
	@echo "Pipeline completed successfully!"

# Clean
clean:
	@echo "Cleaning..."
	mvn clean
	docker rmi $(DOCKER_USERNAME)/$(IMAGE_NAME):$(VERSION) || true
	docker rmi $(DOCKER_USERNAME)/$(IMAGE_NAME):latest || true

# Help
help:
	@echo "Available targets:"
	@echo "  build          - Compile application"
	@echo "  test           - Run tests"
	@echo "  package        - Package JAR"
	@echo "  docker-build   - Build Docker image"
	@echo "  docker-push    - Push Docker image"
	@echo "  security-scan  - Run security scan"
	@echo "  pipeline       - Run full pipeline"
	@echo "  clean          - Clean artifacts"
