.PHONY: help build run stop clean logs test

# Default environment
ENV ?= dev

help: ## Show this help message
	@echo 'Usage: make [target] [ENV=dev|prod|basic]'
	@echo ''
	@echo 'Targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

build: ## Build the Docker image
	@echo "Building Wirebarley for $(ENV) environment..."
	@if [ "$(ENV)" = "dev" ]; then \
		docker-compose -f docker-compose.yml -f docker-compose.dev.yml build; \
	elif [ "$(ENV)" = "prod" ]; then \
		docker-compose -f docker-compose.yml -f docker-compose.prod.yml build; \
	else \
		docker-compose build; \
	fi

run: ## Run the application
	@echo "Starting Wirebarley in $(ENV) environment..."
	@if [ "$(ENV)" = "dev" ]; then \
		docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d; \
	elif [ "$(ENV)" = "prod" ]; then \
		docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d; \
	else \
		docker-compose up -d; \
	fi
	@echo "Waiting for service to start..."
	@sleep 10
	@echo "Service is starting at http://localhost:8080"
	@echo "Swagger UI: http://localhost:8080/swagger-ui.html"

stop: ## Stop the application
	@echo "Stopping Wirebarley..."
	@if [ "$(ENV)" = "dev" ]; then \
		docker-compose -f docker-compose.yml -f docker-compose.dev.yml down; \
	elif [ "$(ENV)" = "prod" ]; then \
		docker-compose -f docker-compose.yml -f docker-compose.prod.yml down; \
	else \
		docker-compose down; \
	fi

clean: ## Clean up containers, volumes, and images
	@echo "Cleaning up Wirebarley..."
	@if [ "$(ENV)" = "dev" ]; then \
		docker-compose -f docker-compose.yml -f docker-compose.dev.yml down -v --remove-orphans; \
	elif [ "$(ENV)" = "prod" ]; then \
		docker-compose -f docker-compose.yml -f docker-compose.prod.yml down -v --remove-orphans; \
	else \
		docker-compose down -v --remove-orphans; \
	fi
	@docker system prune -f

logs: ## Show logs
	@if [ "$(ENV)" = "dev" ]; then \
		docker-compose -f docker-compose.yml -f docker-compose.dev.yml logs -f --tail=100; \
	elif [ "$(ENV)" = "prod" ]; then \
		docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs -f --tail=100; \
	else \
		docker-compose logs -f --tail=100; \
	fi

test: ## Run tests in Docker
	@echo "Running tests..."
	@docker run --rm -v $(PWD):/app -w /app gradle:8.5-jdk21-alpine ./gradlew test

restart: stop run ## Restart the application

status: ## Check application status
	@echo "Checking application status..."
	@docker ps | grep wirebarley || echo "No Wirebarley containers running"
	@echo ""
	@echo "Health check:"
	@curl -s http://localhost:8080/actuator/health | jq . || echo "Service not available"
