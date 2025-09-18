# Cloud Foundry Deployment Guide

This guide explains how to deploy the AudioDB MCP Server to Cloud Foundry.

## Prerequisites

1. **Cloud Foundry CLI** - Install from [here](https://docs.cloudfoundry.org/cf-cli/install-go-cli.html)
2. **Java 21+** - Required for building the application
3. **Maven 3.6+** - For building the project
4. **CF Account** - Access to a Cloud Foundry environment

## Quick Deployment

1. **Login to Cloud Foundry:**
   ```bash
   cf login -a <your-cf-api-endpoint>
   ```

2. **Deploy the application:**
   ```bash
   ./deploy-cf.sh
   ```

## Manual Deployment

If you prefer manual deployment:

1. **Build the application:**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Deploy to Cloud Foundry:**
   ```bash
   cf push
   ```

## Configuration

### Environment Variables

Set these environment variables for production:

```bash
# AudioDB API Key (optional, defaults to test key "2")
cf set-env audiodb-mcp-server AUDIODB_API_KEY your_production_api_key

# Logging levels
cf set-env audiodb-mcp-server LOGGING_LEVEL_COM_AUDIODB_MCP DEBUG

# Restart after setting environment variables
cf restart audiodb-mcp-server
```

### Manifest Configuration

The `manifest.yml` file contains the deployment configuration:

- **Memory:** 1GB (adjust based on needs)
- **Instances:** 1 (can be scaled)
- **Health Check:** HTTP endpoint `/actuator/health`
- **Java Version:** 21+

### Application Profiles

The application uses different profiles:

- **Local:** `application.yml` (development)
- **Cloud:** `application-cloud.yml` (Cloud Foundry)

Cloud Foundry automatically activates the `cloud` profile.

## Endpoints

Once deployed, your application will be available at:

- **MCP Endpoint:** `https://your-app-url.cfapps.io/api/mcp`
- **Health Check:** `https://your-app-url.cfapps.io/actuator/health`
- **Test Endpoints:**
  - Artist: `https://your-app-url.cfapps.io/test/artist?name=coldplay`
  - Album: `https://your-app-url.cfapps.io/test/album?artist=coldplay`
  - Help: `https://your-app-url.cfapps.io/test/help`

## MCP Tools Available

1. **search_artist** - Search for musical artists with biography, sales data, etc.
2. **search_album** - Search for albums by artist (with optional album name filter)
3. **search_track** - Search for tracks/songs by artist and track name

## Monitoring

### View Application Status
```bash
cf app audiodb-mcp-server
```

### View Logs
```bash
# Recent logs
cf logs audiodb-mcp-server --recent

# Tail logs in real-time
cf logs audiodb-mcp-server
```

### Check Health
```bash
curl https://your-app-url.cfapps.io/actuator/health
```

## Scaling

### Scale Instances
```bash
cf scale audiodb-mcp-server -i 2
```

### Scale Memory
```bash
cf scale audiodb-mcp-server -m 2G
```

## Troubleshooting

### Common Issues

1. **Build Failures:**
   - Ensure Java 21+ is installed
   - Run `mvn clean compile` locally first

2. **Deployment Failures:**
   - Check CF CLI is logged in: `cf target`
   - Verify manifest.yml syntax
   - Check application logs: `cf logs audiodb-mcp-server --recent`

3. **Application Not Starting:**
   - Check health endpoint is responding
   - Verify PORT environment variable is being used
   - Check for Java version compatibility

### Debug Commands

```bash
# SSH into the application container
cf ssh audiodb-mcp-server

# Check environment variables
cf env audiodb-mcp-server

# View application events
cf events audiodb-mcp-server
```

## Security

- The application uses HTTPS by default in Cloud Foundry
- Health check endpoint is publicly accessible
- Consider setting up proper authentication for production use
- API keys should be set via environment variables, not hardcoded

## Support

For issues related to:
- **AudioDB API:** Check [TheAudioDB.com](https://www.theaudiodb.com/api_guide.php)
- **Cloud Foundry:** Check your CF provider documentation
- **Spring Boot:** Check [Spring Boot documentation](https://spring.io/projects/spring-boot)