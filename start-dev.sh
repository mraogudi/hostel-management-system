#!/bin/bash

echo "🚀 Starting Hostel Management System Development Environment"
echo "============================================================"
echo ""

# Check if MongoDB is running
echo "📦 Checking MongoDB connection..."
if ! mongosh --eval "db.runCommand('ping').ok" --quiet > /dev/null 2>&1; then
    echo "❌ MongoDB is not running. Please start MongoDB first:"
    echo "   - Windows: net start MongoDB"
    echo "   - macOS: brew services start mongodb/brew/mongodb-community"
    echo "   - Linux: sudo systemctl start mongod"
    exit 1
fi
echo "✅ MongoDB is running"
echo ""

# Check if Java is available
echo "☕ Checking Java installation..."
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi
echo "✅ Java is available"
echo ""

# Check if Maven is available
echo "🔨 Checking Maven installation..."
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.6 or higher."
    exit 1
fi
echo "✅ Maven is available"
echo ""

# Check if Node.js is available
echo "🟢 Checking Node.js installation..."
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js 16 or higher."
    exit 1
fi
echo "✅ Node.js is available"
echo ""

echo "🔧 Installing dependencies..."

# Install backend dependencies
echo "📦 Installing Spring Boot dependencies..."
cd server-spring-boot
if ! mvn clean install -q; then
    echo "❌ Failed to install backend dependencies"
    exit 1
fi
cd ..

# Install frontend dependencies
echo "📦 Installing React dependencies..."
cd client
if ! npm install --silent; then
    echo "❌ Failed to install frontend dependencies"
    exit 1
fi
cd ..

echo "✅ All dependencies installed successfully!"
echo ""

echo "🚀 Starting development servers..."
echo ""
echo "Backend will start on: http://localhost:8080"
echo "Frontend will start on: http://localhost:3000"
echo ""
echo "Opening in separate terminal windows..."
echo "Press Ctrl+C in each terminal to stop the servers"
echo ""

# Start backend in a new terminal (macOS/Linux)
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    osascript -e 'tell app "Terminal" to do script "cd '$(pwd)'/server-spring-boot && mvn spring-boot:run"'
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    if command -v gnome-terminal &> /dev/null; then
        gnome-terminal -- bash -c "cd server-spring-boot && mvn spring-boot:run; exec bash"
    elif command -v xterm &> /dev/null; then
        xterm -e "cd server-spring-boot && mvn spring-boot:run" &
    else
        echo "⚠️  Please manually run: cd server-spring-boot && mvn spring-boot:run"
    fi
fi

# Wait a bit for backend to start
sleep 3

# Start frontend in a new terminal
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    osascript -e 'tell app "Terminal" to do script "cd '$(pwd)'/client && npm start"'
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    if command -v gnome-terminal &> /dev/null; then
        gnome-terminal -- bash -c "cd client && npm start; exec bash"
    elif command -v xterm &> /dev/null; then
        xterm -e "cd client && npm start" &
    else
        echo "⚠️  Please manually run: cd client && npm start"
    fi
fi

echo "🎉 Development environment started!"
echo ""
echo "Access the application at: http://localhost:3000"
echo "Default warden credentials:"
echo "  Username: warden"
echo "  Password: warden123"
echo ""
echo "Happy coding! 🚀" 