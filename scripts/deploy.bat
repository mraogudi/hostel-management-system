@echo off
REM Hostel Management System Deployment Script for Windows
REM This script deploys all four applications using Docker Compose

setlocal enabledelayedexpansion

REM Configuration
set PROJECT_NAME=hostel-management
set ENVIRONMENT=%1
if "%ENVIRONMENT%"=="" set ENVIRONMENT=production
set FORCE_REBUILD=%2
if "%FORCE_REBUILD%"=="" set FORCE_REBUILD=false

echo.
echo ==========================================
echo   Hostel Management System Deployment
echo ==========================================
echo Environment: %ENVIRONMENT%
echo Force Rebuild: %FORCE_REBUILD%
echo.

REM Function to check if command exists
where docker >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not installed. Please install Docker Desktop first.
    echo Download from: https://www.docker.com/products/docker-desktop/
    pause
    exit /b 1
)

where docker-compose >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker Compose is not installed. Please install Docker Compose first.
    pause
    exit /b 1
)

REM Check if Docker daemon is running
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker daemon is not running. Please start Docker Desktop first.
    pause
    exit /b 1
)

echo [INFO] Prerequisites check passed

REM Setup environment
echo [INFO] Setting up environment...

if not exist .env (
    if exist env.example (
        copy env.example .env >nul
        echo [WARNING] Created .env file from env.example. Please review and update the configuration.
    ) else (
        echo [ERROR] .env file not found and env.example doesn't exist.
        pause
        exit /b 1
    )
)

REM Create necessary directories
if not exist logs mkdir logs
if not exist data mkdir data
if not exist data\mongodb mkdir data\mongodb
if not exist data\mysql mkdir data\mysql
if not exist data\redis mkdir data\redis
if not exist data\backend mkdir data\backend

echo [SUCCESS] Environment setup completed

REM Build images
echo [INFO] Building Docker images...

if "%FORCE_REBUILD%"=="true" (
    echo [INFO] Force rebuilding all images...
    docker-compose build --no-cache
) else (
    docker-compose build
)

if errorlevel 1 (
    echo [ERROR] Failed to build Docker images
    pause
    exit /b 1
)

echo [SUCCESS] Docker images built successfully

REM Start services
echo [INFO] Starting services...

if "%ENVIRONMENT%"=="development" (
    echo [INFO] Starting in development mode...
    docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
) else if "%ENVIRONMENT%"=="dev" (
    echo [INFO] Starting in development mode...
    docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
) else if "%ENVIRONMENT%"=="staging" (
    echo [INFO] Starting in staging mode...
    docker-compose -f docker-compose.yml up -d
) else if "%ENVIRONMENT%"=="production" (
    echo [INFO] Starting in production mode...
    docker-compose -f docker-compose.yml up -d
) else if "%ENVIRONMENT%"=="prod" (
    echo [INFO] Starting in production mode...
    docker-compose -f docker-compose.yml up -d
) else (
    echo [ERROR] Unknown environment: %ENVIRONMENT%
    echo [INFO] Supported environments: development, staging, production
    pause
    exit /b 1
)

if errorlevel 1 (
    echo [ERROR] Failed to start services
    docker-compose ps
    pause
    exit /b 1
)

echo [SUCCESS] Services started successfully

REM Wait for services to be healthy
echo [INFO] Waiting for services to become healthy...
timeout /t 30 /nobreak >nul

REM Run basic health checks
echo [INFO] Running health checks...

REM Check frontend
curl -f http://localhost:3000 >nul 2>&1
if not errorlevel 1 (
    echo [SUCCESS] Frontend is responding
) else (
    echo [WARNING] Frontend health check failed
)

REM Check Node.js backend
curl -f http://localhost:5000/api/health >nul 2>&1
if not errorlevel 1 (
    echo [SUCCESS] Node.js backend is responding
) else (
    echo [WARNING] Node.js backend health check failed
)

REM Check Spring Boot MongoDB
curl -f http://localhost:8080/actuator/health >nul 2>&1
if not errorlevel 1 (
    echo [SUCCESS] Spring Boot MongoDB is responding
) else (
    echo [WARNING] Spring Boot MongoDB health check failed
)

REM Check Spring Boot MySQL
curl -f http://localhost:8081/actuator/health >nul 2>&1
if not errorlevel 1 (
    echo [SUCCESS] Spring Boot MySQL is responding
) else (
    echo [WARNING] Spring Boot MySQL health check failed
)

REM Check Nginx load balancer
curl -f http://localhost/health >nul 2>&1
if not errorlevel 1 (
    echo [SUCCESS] Nginx load balancer is responding
) else (
    echo [WARNING] Nginx load balancer health check failed
)

REM Show summary
echo.
echo [SUCCESS] Deployment completed successfully!
echo.
echo Service URLs:
echo   Frontend:              http://localhost:3000
echo   Node.js API:           http://localhost:5000
echo   Spring Boot MongoDB:   http://localhost:8080
echo   Spring Boot MySQL:     http://localhost:8081
echo   Load Balancer:         http://localhost
echo.
echo Database Access:
echo   MongoDB:               mongodb://localhost:27017
echo   MySQL:                 mysql://localhost:3306
echo   Redis:                 redis://localhost:6379
echo.
echo Default Credentials:
echo   Username: warden
echo   Password: warden123
echo.
echo Service Status:
docker-compose ps
echo.
echo Useful Commands:
echo   View logs:     docker-compose logs -f [service_name]
echo   Stop services: docker-compose down
echo   Restart:       docker-compose restart
echo.
pause 