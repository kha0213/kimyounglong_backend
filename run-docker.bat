@echo off
setlocal enabledelayedexpansion

:: Default environment
set ENV=%1
if "%ENV%"=="" set ENV=dev

echo Wirebarley Docker Deployment Script
echo Environment: %ENV%
echo.

:: Check if Docker is running
docker info >nul 2>&1
if errorlevel 1 (
    echo Error: Docker is not running. Please start Docker Desktop first.
    exit /b 1
)
echo Docker is running

:: Clean up function
:cleanup
set /p CLEANUP="Do you want to clean up existing containers? (y/N) "
if /i "%CLEANUP%"=="y" (
    echo Cleaning up old containers and images...
    if "%ENV%"=="dev" (
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml down --volumes --remove-orphans
    ) else if "%ENV%"=="prod" (
        docker-compose -f docker-compose.yml -f docker-compose.prod.yml down --volumes --remove-orphans
    ) else (
        docker-compose down --volumes --remove-orphans
    )
    docker system prune -f
)

:: Build and run
echo Building Wirebarley application...
if "%ENV%"=="dev" (
    docker-compose -f docker-compose.yml -f docker-compose.dev.yml build
    echo Starting development environment...
    docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
) else if "%ENV%"=="prod" (
    docker-compose -f docker-compose.yml -f docker-compose.prod.yml build
    echo Starting production environment...
    docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
) else if "%ENV%"=="basic" (
    docker-compose build
    echo Starting basic environment...
    docker-compose up -d
) else (
    echo Invalid environment: %ENV%
    echo Usage: run-docker.bat [dev^|prod^|basic]
    exit /b 1
)

:: Wait for service to start
echo Waiting for service to be healthy...
timeout /t 15 /nobreak >nul

:: Check health
set MAX_ATTEMPTS=30
set ATTEMPT=0

:healthcheck
if %ATTEMPT% geq %MAX_ATTEMPTS% goto healthfailed

curl -f http://localhost:8080/actuator/health >nul 2>&1
if %errorlevel%==0 (
    echo Service is healthy!
    echo Access the application at: http://localhost:8080
    echo Swagger UI: http://localhost:8080/swagger-ui.html
    if "%ENV%"=="dev" (
        echo H2 Console: http://localhost:8080/h2-console
        echo Debug port: 5005
    )
    goto showlogs
)

echo Waiting for service to start... (attempt %ATTEMPT%/%MAX_ATTEMPTS%)
timeout /t 2 /nobreak >nul
set /a ATTEMPT+=1
goto healthcheck

:healthfailed
echo Service failed to start. Check logs for details.
if "%ENV%"=="dev" (
    docker-compose -f docker-compose.yml -f docker-compose.dev.yml logs --tail=100
) else if "%ENV%"=="prod" (
    docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs --tail=100
) else (
    docker-compose logs --tail=100
)
exit /b 1

:showlogs
set /p SHOWLOGS="Do you want to see the logs? (y/N) "
if /i "%SHOWLOGS%"=="y" (
    echo Showing logs...
    if "%ENV%"=="dev" (
        docker-compose -f docker-compose.yml -f docker-compose.dev.yml logs -f --tail=100
    ) else if "%ENV%"=="prod" (
        docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs -f --tail=100
    ) else (
        docker-compose logs -f --tail=100
    )
)

endlocal
