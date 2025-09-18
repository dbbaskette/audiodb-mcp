#!/bin/bash

# AudioDB MCP Server Startup Script
# This script starts the AudioDB Model Context Protocol server locally

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SERVER_PORT=8090
PROJECT_NAME="AudioDB MCP Server"
JAR_NAME="audiodb-mcp-server-1.0.0.jar"
PID_FILE="mcp-server.pid"

echo -e "${BLUE}🎵 Starting $PROJECT_NAME${NC}"
echo "=================================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Error: Java is not installed or not in PATH${NC}"
    echo "Please install Java 21 or later"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo -e "${YELLOW}⚠️  Warning: Java $JAVA_VERSION detected. Java 21+ is recommended${NC}"
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Error: Maven is not installed or not in PATH${NC}"
    echo "Please install Maven 3.6 or later"
    exit 1
fi

# Check if port is already in use
if lsof -Pi :$SERVER_PORT -sTCP:LISTEN -t >/dev/null; then
    echo -e "${YELLOW}⚠️  Port $SERVER_PORT is already in use${NC}"
    echo "Checking if it's our MCP server..."

    if [ -f "$PID_FILE" ]; then
        OLD_PID=$(cat "$PID_FILE")
        if ps -p "$OLD_PID" > /dev/null; then
            echo -e "${YELLOW}🔄 Stopping existing MCP server (PID: $OLD_PID)${NC}"
            kill "$OLD_PID" 2>/dev/null || true
            sleep 2
        fi
        rm -f "$PID_FILE"
    fi

    # Wait a bit more if port is still in use
    if lsof -Pi :$SERVER_PORT -sTCP:LISTEN -t >/dev/null; then
        echo -e "${RED}❌ Port $SERVER_PORT is still in use by another process${NC}"
        echo "Please free up port $SERVER_PORT or change the server port in application.yml"
        exit 1
    fi
fi

# Function to cleanup on exit
cleanup() {
    echo -e "\n${YELLOW}🛑 Shutting down MCP server...${NC}"
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p "$PID" > /dev/null; then
            kill "$PID" 2>/dev/null || true
            echo -e "${GREEN}✅ MCP server stopped${NC}"
        fi
        rm -f "$PID_FILE"
    fi
    exit 0
}

# Set up signal handlers
trap cleanup SIGINT SIGTERM

echo -e "${BLUE}🔧 Building project...${NC}"
mvn clean compile -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Build successful${NC}"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

echo -e "${BLUE}🚀 Starting MCP server on port $SERVER_PORT...${NC}"

# Start the server and capture PID
mvn spring-boot:run -q &
SERVER_PID=$!

# Store PID for cleanup
echo $SERVER_PID > "$PID_FILE"

# Wait for server to start
echo -e "${YELLOW}⏳ Waiting for server to start...${NC}"
sleep 3

# Check if server is running
MAX_ATTEMPTS=30
ATTEMPT=0
while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if curl -s "http://localhost:$SERVER_PORT/actuator/health" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ MCP server is running!${NC}"
        break
    fi
    ATTEMPT=$((ATTEMPT + 1))
    sleep 1
    echo -ne "${YELLOW}⏳ Waiting... ($ATTEMPT/$MAX_ATTEMPTS)\r${NC}"
done

if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
    echo -e "${RED}❌ Server failed to start within expected time${NC}"
    cleanup
fi

echo ""
echo -e "${GREEN}🎉 AudioDB MCP Server is running successfully!${NC}"
echo "=================================================="
echo -e "${BLUE}📍 Server URL:${NC} http://localhost:$SERVER_PORT"
echo -e "${BLUE}🏥 Health Check:${NC} http://localhost:$SERVER_PORT/actuator/health"
echo -e "${BLUE}🛠️  MCP Endpoint:${NC} http://localhost:$SERVER_PORT/api/mcp"
echo ""
echo -e "${YELLOW}🧪 Test Endpoints:${NC}"
echo "   Artist Search:    curl \"http://localhost:$SERVER_PORT/test/artist?name=coldplay\""
echo "   Album Search:     curl \"http://localhost:$SERVER_PORT/test/album?artist=coldplay&album=parachutes\""
echo "   Help:             curl \"http://localhost:$SERVER_PORT/test/help\""
echo ""
echo -e "${BLUE}📚 Available MCP Tools:${NC}"
echo "   • search_artist           - Search for musical artists (with sales data, biography, etc.)"
echo "   • search_album            - Search for albums by artist (with optional album name filter)"
echo "   • search_track            - Search for tracks/songs by artist and track name"
echo ""
echo -e "${YELLOW}Press Ctrl+C to stop the server${NC}"
echo "=================================================="

# Keep the script running and wait for the server process
wait $SERVER_PID