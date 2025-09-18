# AudioDB MCP Server

A Model Context Protocol (MCP) server that provides access to TheAudioDB.com API for music artist information and discography data.

## Features

- **Artist Search**: Find detailed information about musical artists
- **Artist Discography**: Get complete album listings for artists
- **Reactive Architecture**: Built on Spring Boot WebFlux for high performance
- **MCP Protocol**: Compatible with any MCP client

## Quick Start

### Prerequisites

- Java 21 or later
- Maven 3.6 or later

### Running the Server

```bash
# Make the script executable (first time only)
chmod +x run-mcp.sh

# Start the MCP server
./run-mcp.sh
```

The server will start on port `8090` by default.

### Testing the Server

Once running, you can test the MCP tools using these HTTP endpoints:

```bash
# Search for an artist
curl "http://localhost:8090/test/artist?name=coldplay"

# Get artist discography
curl "http://localhost:8090/test/discography?artist=radiohead"

# Get help
curl "http://localhost:8090/test/help"

# Health check
curl "http://localhost:8090/actuator/health"
```

## MCP Tools

The server exposes these MCP tools:

### `search_artist`
Searches for a musical artist by name and returns detailed information.

**Parameters:**
- `artistName` (required): The name of the artist to search for

**Returns:**
- Artist name, genre, style, formed year
- Biography and record label information
- Thumbnail and logo image URLs
- MusicBrainz ID for integration

### `get_artist_discography`
Retrieves the complete discography for a given artist.

**Parameters:**
- `artistName` (required): The name of the artist

**Returns:**
- List of albums with release years
- Album genres and record labels
- Album cover image URLs
- Album descriptions when available

## Configuration

Edit `src/main/resources/application.yml` to customize:

- Server port (`server.port`)
- TheAudioDB API key (`audiodb.api.api-key`)
- MCP endpoint path (`spring.ai.mcp.server.streamable-http.mcp-endpoint`)

## Development

### Manual Build & Run

```bash
# Build the project
mvn clean compile

# Run with Maven
mvn spring-boot:run

# Or build JAR and run
mvn clean package
java -jar target/audiodb-mcp-server-1.0.0.jar
```

### Project Structure

```
src/main/java/com/audiodb/mcp/
├── AudioDbMcpServerApplication.java  # Main application
├── controller/
│   └── TestController.java           # HTTP test endpoints
├── model/
│   ├── Artist.java                   # Artist data model
│   ├── Album.java                    # Album data model
│   ├── ArtistSearchResponse.java     # API response wrapper
│   └── DiscographyResponse.java      # API response wrapper
└── service/
    ├── AudioDbToolService.java       # MCP tools implementation
    ├── TheAudioDbClient.java         # Reactive API client
    └── SimpleAudioDbClient.java      # Non-reactive client for testing
```

## API Integration

This server integrates with [TheAudioDB.com](https://www.theaudiodb.com/) free music API:

- **Rate Limit**: 2 calls per second with test API key
- **Test Key**: Uses API key "2" by default
- **Endpoints**: Artist search and discography retrieval
- **Data**: High-quality metadata and artwork URLs

## License

This project is for educational and development purposes. Please respect TheAudioDB.com's terms of service and rate limits.