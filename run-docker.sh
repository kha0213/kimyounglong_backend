#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Default environment
ENV=${1:-dev}

echo -e "${GREEN}Wirebarley Docker Deployment Script${NC}"
echo -e "${YELLOW}Environment: $ENV${NC}"
echo ""

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        echo -e "${RED}Docker is not running. Please start Docker first.${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ Docker is running${NC}"
}

# Function to clean up
cleanup() {
    echo -e "${YELLOW}Cleaning up old containers and images...${NC}"
    docker-compose -f docker-compose.yml -f docker-compose.$ENV.yml down --volumes --remove-orphans
    docker system prune -f
}

# Function to build and run
build_and_run() {
    echo -e "${YELLOW}Building Wirebarley application...${NC}"
    
    case $ENV in
        "dev")
            docker-compose -f docker-compose.yml -f docker-compose.dev.yml build
            echo -e "${GREEN}Starting development environment...${NC}"
            docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
            ;;
        "prod")
            docker-compose -f docker-compose.yml -f docker-compose.prod.yml build
            echo -e "${GREEN}Starting production environment...${NC}"
            docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
            ;;
        "basic")
            docker-compose build
            echo -e "${GREEN}Starting basic environment...${NC}"
            docker-compose up -d
            ;;
        *)
            echo -e "${RED}Invalid environment: $ENV${NC}"
            echo "Usage: $0 [dev|prod|basic]"
            exit 1
            ;;
    esac
}

# Function to show logs
show_logs() {
    echo -e "${YELLOW}Showing logs...${NC}"
    case $ENV in
        "dev")
            docker-compose -f docker-compose.yml -f docker-compose.dev.yml logs -f --tail=100
            ;;
        "prod")
            docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs -f --tail=100
            ;;
        "basic")
            docker-compose logs -f --tail=100
            ;;
    esac
}

# Function to check service health
check_health() {
    echo -e "${YELLOW}Waiting for service to be healthy...${NC}"
    
    # Wait for container to be running
    sleep 10
    
    # Check health status
    max_attempts=30
    attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
            echo -e "${GREEN}✓ Service is healthy!${NC}"
            echo -e "${GREEN}Access the application at: http://localhost:8080${NC}"
            echo -e "${GREEN}Swagger UI: http://localhost:8080/swagger-ui.html${NC}"
            if [ "$ENV" == "dev" ]; then
                echo -e "${GREEN}H2 Console: http://localhost:8080/h2-console${NC}"
                echo -e "${GREEN}Debug port: 5005${NC}"
            fi
            return 0
        fi
        
        echo -e "${YELLOW}Waiting for service to start... (attempt $((attempt+1))/$max_attempts)${NC}"
        sleep 2
        attempt=$((attempt+1))
    done
    
    echo -e "${RED}Service failed to start. Check logs for details.${NC}"
    show_logs
    return 1
}

# Main execution
main() {
    check_docker
    
    # Ask for cleanup
    read -p "Do you want to clean up existing containers? (y/N) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        cleanup
    fi
    
    # Build and run
    build_and_run
    
    # Check health
    check_health
    
    # Ask to show logs
    read -p "Do you want to see the logs? (y/N) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        show_logs
    fi
}

# Run main function
main
