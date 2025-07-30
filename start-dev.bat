@echo off
echo 🚀 Starting Hostel Management System Development Environment
echo ============================================================
echo.

REM Check if MongoDB is running
echo 📦 Checking MongoDB connection...
mongosh --eval "db.runCommand('ping').ok" --quiet >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ MongoDB is not running. Please start MongoDB first:
    echo    net start MongoDB
    pause
    exit /b 1
)
echo ✅ MongoDB is running
echo.

REM Check if Java is available
echo ☕ Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java is not installed. Please install Java 17 or higher.
    pause
    exit /b 1
)
echo ✅ Java is available
echo.

REM Check if Maven is available
echo 🔨 Checking Maven installation...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven is not installed. Please install Maven 3.6 or higher.
    pause
    exit /b 1
)
echo ✅ Maven is available
echo.

REM Check if Node.js is available
echo 🟢 Checking Node.js installation...
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Node.js is not installed. Please install Node.js 16 or higher.
    pause
    exit /b 1
)
echo ✅ Node.js is available
echo.

echo 🔧 Installing dependencies...

REM Install backend dependencies
echo 📦 Installing Spring Boot dependencies...
cd server-spring-boot
call mvn clean install -q
if %errorlevel% neq 0 (
    echo ❌ Failed to install backend dependencies
    pause
    exit /b 1
)
cd ..

REM Install frontend dependencies
echo 📦 Installing React dependencies...
cd client
call npm install --silent
if %errorlevel% neq 0 (
    echo ❌ Failed to install frontend dependencies
    pause
    exit /b 1
)
cd ..

echo ✅ All dependencies installed successfully!
echo.

echo 🚀 Starting development servers...
echo.
echo Backend will start on: http://localhost:8080
echo Frontend will start on: http://localhost:3000
echo.
echo Opening in separate command windows...
echo Press Ctrl+C in each window to stop the servers
echo.

REM Start backend in a new window
start "Spring Boot Backend" cmd /k "cd server-spring-boot && mvn spring-boot:run"

REM Wait a bit for backend to start
timeout /t 3 /nobreak >nul

REM Start frontend in a new window
start "React Frontend" cmd /k "cd client && npm start"

echo 🎉 Development environment started!
echo.
echo Access the application at: http://localhost:3000
echo Default warden credentials:
echo   Username: warden
echo   Password: warden123
echo.
echo Happy coding! 🚀
pause 