# Hostel Management System

A comprehensive hostel management system with multiple backend implementations and a modern React frontend. Choose the backend that best fits your needs: Node.js/Express with JSON storage, Spring Boot with MongoDB, or Spring Boot with MySQL.

## 🏗️ System Overview

```
┌─────────────────────────────────────────────────────────────┐
│                 Hostel Management System                   │
└─────────────────────────────────────────────────────────────┘
│
├── 🎨 Frontend (React)
│   ├── Student Dashboard (Room info, requests, menu)
│   ├── Warden Dashboard (Statistics, management, requests)
│   └── Authentication & Role-based Access
│
└── 🚀 Backend Options (Choose One)
    ├── Node.js/Express + JSON (Recommended for Development)
    ├── Spring Boot + MongoDB (Enterprise NoSQL)
    └── Spring Boot + MySQL (Enterprise SQL)
```

## 📱 Applications

### 🎨 Frontend Application
**React-based modern UI with comprehensive dashboards**

📍 **[Frontend Documentation](client/README.md)**

- **Technology**: React 18, Context API, Custom CSS
- **Features**: Student & Warden dashboards, responsive design, real-time updates
- **Port**: http://localhost:3000

### 🚀 Backend Options

#### 1. Node.js/Express Backend (Recommended)
**Fast development with JSON file-based storage**

📍 **[Node.js Backend Documentation](server/README.md)**

- **Technology**: Node.js, Express.js, JWT, JSON storage
- **Features**: Quick setup, no database required, real-time persistence
- **Port**: http://localhost:5000
- **Best For**: Development, prototyping, small deployments

#### 2. Spring Boot + MongoDB Backend
**Enterprise NoSQL solution with document-based storage**

📍 **[Spring Boot MongoDB Documentation](server-spring-boot/README.md)**

- **Technology**: Spring Boot 3.2, MongoDB, Spring Security, JWT
- **Features**: Layered architecture, service layer separation, document storage
- **Port**: http://localhost:8080
- **Best For**: Scalable applications, document-based data, enterprise features

#### 3. Spring Boot + MySQL Backend
**Production-ready SQL solution with relational integrity**

📍 **[Spring Boot MySQL Documentation](server-spring-boot-mysql/README.md)**

- **Technology**: Spring Boot 3.2, MySQL 8.0, JPA/Hibernate, Flyway migrations
- **Features**: ACID compliance, relational integrity, production monitoring
- **Port**: http://localhost:8080
- **Best For**: Production deployments, complex queries, enterprise compliance

## 🚀 Quick Start Guide

### Option 1: Node.js/Express (Fastest Setup)
```bash
# Terminal 1 - Backend
cd server
npm install && npm start

# Terminal 2 - Frontend
cd client
npm install && npm start

# Access: http://localhost:3000
```

### Option 2: Spring Boot + MongoDB
```bash
# Prerequisites: Java 17+, Maven 3.6+, MongoDB 4.4+

# Start MongoDB
sudo systemctl start mongod

# Terminal 1 - Backend
cd server-spring-boot
mvn clean install && mvn spring-boot:run

# Terminal 2 - Frontend
cd client
npm install && npm start

# Access: http://localhost:3000
```

### Option 3: Spring Boot + MySQL
```bash
# Prerequisites: Java 17+, Maven 3.6+, MySQL 8.0+

# Setup MySQL
mysql -u root -p
CREATE DATABASE hostel_management;
EXIT;

# Terminal 1 - Backend
cd server-spring-boot-mysql
mvn clean install && mvn spring-boot:run

# Terminal 2 - Frontend
cd client
npm install && npm start

# Access: http://localhost:3000
```

## 🔑 Default Credentials

All implementations include default credentials:

- **Warden**: 
  - Username: `warden`
  - Password: `warden123`

- **Students**: Created by wardens with auto-generated passwords

## 📊 Feature Comparison

| Feature | Node.js/Express | Spring Boot + MongoDB | Spring Boot + MySQL |
|---------|-----------------|----------------------|-------------------|
| **Setup Time** | ⚡ Fastest | 🔄 Medium | 🔄 Medium |
| **Database** | JSON File | MongoDB | MySQL |
| **Performance** | ✅ Good | ✅ Excellent | ✅ Excellent |
| **Scalability** | 🔄 Limited | ✅ High | ✅ Very High |
| **Enterprise** | ❌ Basic | ✅ Advanced | ✅ Enterprise |
| **Transactions** | ❌ No | 🔄 Limited | ✅ ACID |
| **Queries** | ❌ Basic | ✅ Flexible | ✅ Complex SQL |
| **Production** | 🔄 Small Scale | ✅ Yes | ✅ Enterprise |

## 🎯 Choose Your Backend

### 🟢 Node.js/Express - Choose If:
- Quick prototyping or development
- No database setup preferred
- Small to medium user base
- Simple deployment requirements
- Learning/educational purposes

### 🟡 Spring Boot + MongoDB - Choose If:
- Document-based data model fits your needs
- Need enterprise features with flexibility
- Horizontal scaling requirements
- Complex nested data structures
- Modern microservices architecture

### 🟢 Spring Boot + MySQL - Choose If:
- Production enterprise deployment
- Strong data consistency requirements
- Complex relational queries needed
- Compliance and audit requirements
- Integration with existing SQL infrastructure

## 📋 Common Features (All Backends)

### For Students
- 🏠 **Dashboard**: Profile, room info, roommate details
- 🔄 **Room Requests**: Visual room selection with bed choice
- 🆔 **Profile Updates**: Request personal details changes
- 🍽️ **Food Menu**: Weekly meal schedules
- 📞 **Contact Warden**: Easy access to warden information

### For Wardens
- 📊 **Analytics Dashboard**: 6 comprehensive stat cards
- 👥 **Student Management**: Create, edit, assign rooms
- 🏠 **Room Management**: Visual room cards, bed assignments
- 📋 **Request Processing**: Approve/reject room and profile requests
- 📈 **Statistics**: Real-time occupancy and request tracking

## 🛠️ Development Scripts

Each application includes development helper scripts:

### Cross-Platform Development
```bash
# Windows
start-dev.bat

# Linux/Mac
./start-dev.sh
```

### Individual Application Commands
```bash
# Frontend only
cd client && npm start

# Node.js Backend only
cd server && npm start

# Spring Boot MongoDB only
cd server-spring-boot && mvn spring-boot:run

# Spring Boot MySQL only
cd server-spring-boot-mysql && mvn spring-boot:run
```

## 🔧 Configuration

Each backend can be configured independently:

- **Node.js**: Environment variables or defaults
- **Spring Boot**: `application.yml` with profiles (dev, prod)
- **Database**: Connection strings and credentials

## 📚 Documentation Structure

```
📁 Documentation
├── 📄 README.md (This file - Overview)
├── 📁 client/
│   └── 📄 README.md (React Frontend)
├── 📁 server/
│   └── 📄 README.md (Node.js Backend)
├── 📁 server-spring-boot/
│   └── 📄 README.md (Spring Boot MongoDB)
└── 📁 server-spring-boot-mysql/
    └── 📄 README.md (Spring Boot MySQL)
```

## 🚨 Troubleshooting

### Port Conflicts
- **Frontend**: Runs on port 3000
- **Node.js Backend**: Runs on port 5000
- **Spring Boot Backends**: Run on port 8080

### Common Issues
1. **CORS Errors**: Ensure backend is running before frontend
2. **Database Connection**: Check database service status
3. **Dependencies**: Run `npm install` or `mvn clean install`
4. **Java Version**: Ensure Java 17+ for Spring Boot applications

## 📈 Recent Updates

### v2.0 - Multi-Backend Architecture
- ✅ Added Spring Boot MongoDB implementation
- ✅ Added Spring Boot MySQL implementation  
- ✅ Enhanced request management system
- ✅ Comprehensive documentation split by application

### v1.5 - Enhanced Features
- ✅ Personal details update requests
- ✅ Room change requests with bed selection
- ✅ Secondary stats grid with 6 stat cards
- ✅ Modern responsive UI improvements

### v1.0 - Core System
- ✅ Authentication and role-based access
- ✅ Room and bed management
- ✅ Student registration and management
- ✅ Food menu system

## 📝 Contributing

1. Choose your preferred backend implementation
2. Read the specific application documentation
3. Follow the setup instructions for your chosen stack
4. Make changes and test thoroughly
5. Update relevant documentation

## 📝 License

This project is built for educational purposes and hostel management automation.

---

🚀 **Ready to Start?** Choose your preferred backend implementation and follow the specific documentation links above for detailed setup instructions! 