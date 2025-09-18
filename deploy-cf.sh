#!/bin/bash

# AudioDB MCP Server - Cloud Foundry Deployment Script
# This script builds and deploys the AudioDB MCP Server to Cloud Foundry

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="AudioDB MCP Server"
APP_NAME="audiodb-mcp-server"
JAR_NAME="audiodb-mcp-server-2.0.1.jar"

echo -e "${BLUE}🚀 Deploying $PROJECT_NAME to Cloud Foundry${NC}"
echo "=============================================="

# Check if CF CLI is installed
if ! command -v cf &> /dev/null; then
    echo -e "${RED}❌ Error: Cloud Foundry CLI is not installed${NC}"
    echo "Please install CF CLI: https://docs.cloudfoundry.org/cf-cli/install-go-cli.html"
    exit 1
fi

# Check if logged into CF
if ! cf target &> /dev/null; then
    echo -e "${RED}❌ Error: Not logged into Cloud Foundry${NC}"
    echo "Please login with: cf login -a <api-endpoint>"
    exit 1
fi

# Display current target
echo -e "${BLUE}📍 Current CF Target:${NC}"
cf target

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Error: Java is not installed${NC}"
    echo "Please install Java 21 or later"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Error: Maven is not installed${NC}"
    echo "Please install Maven 3.6 or later"
    exit 1
fi

echo -e "${BLUE}🔧 Building application...${NC}"
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Build successful${NC}"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

# Check if JAR file exists
if [ ! -f "target/$JAR_NAME" ]; then
    echo -e "${RED}❌ Error: JAR file not found: target/$JAR_NAME${NC}"
    exit 1
fi

echo -e "${BLUE}🌐 Deploying to Cloud Foundry...${NC}"

# Deploy application
cf push

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Deployment successful!${NC}"

    # Get application info
    echo -e "${BLUE}📱 Application Information:${NC}"
    cf app $APP_NAME

    # Get routes
    echo -e "${BLUE}🌐 Application Routes:${NC}"
    cf routes | grep $APP_NAME

    # Show logs command
    echo ""
    echo -e "${YELLOW}📋 Useful Commands:${NC}"
    echo "  View logs:        cf logs $APP_NAME --recent"
    echo "  Tail logs:        cf logs $APP_NAME"
    echo "  App status:       cf app $APP_NAME"
    echo "  Restart app:      cf restart $APP_NAME"
    echo "  Set env var:      cf set-env $APP_NAME AUDIODB_API_KEY your_api_key"
    echo "  SSH to app:       cf ssh $APP_NAME"
    echo ""
    echo -e "${GREEN}🎉 AudioDB MCP Server is now running on Cloud Foundry!${NC}"

else
    echo -e "${RED}❌ Deployment failed${NC}"
    echo "Check the logs with: cf logs $APP_NAME --recent"
    exit 1
fi