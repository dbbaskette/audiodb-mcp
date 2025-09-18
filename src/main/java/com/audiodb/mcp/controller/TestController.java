package com.audiodb.mcp.controller;

import com.audiodb.mcp.service.AudioDbToolService;
import com.audiodb.mcp.service.SimpleAudioDbClient;
import com.audiodb.mcp.model.Artist;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/test")
public class TestController {

    private final AudioDbToolService toolService;
    private final SimpleAudioDbClient simpleClient;

    public TestController(AudioDbToolService toolService, SimpleAudioDbClient simpleClient) {
        this.toolService = toolService;
        this.simpleClient = simpleClient;
    }

    @GetMapping("/artist")
    public ResponseEntity<String> testSearchArtist(@RequestParam("name") String name) {
        try {
            Artist artist = simpleClient.searchArtist(name);
            if (artist == null || artist.getName() == null) {
                return ResponseEntity.ok()
                    .header("Content-Type", "text/plain; charset=utf-8")
                    .body(String.format("No artist found for search term: '%s'", name));
            }

            StringBuilder result = new StringBuilder();
            result.append("Artist Information:\n");
            result.append("==================\n");
            result.append(String.format("Name: %s\n", artist.getName()));

            if (artist.getGenre() != null) {
                result.append(String.format("Genre: %s\n", artist.getGenre()));
            }
            if (artist.getStyle() != null) {
                result.append(String.format("Style: %s\n", artist.getStyle()));
            }
            if (artist.getFormedYear() != null) {
                result.append(String.format("Formed Year: %s\n", artist.getFormedYear()));
            }
            if (artist.strLabel() != null) {
                result.append(String.format("Label: %s\n", artist.strLabel()));
            }
            if (artist.getBiography() != null && !artist.getBiography().trim().isEmpty()) {
                result.append(String.format("\nBiography:\n%s\n",
                    artist.getBiography().length() > 500 ?
                    artist.getBiography().substring(0, 500) + "..." :
                    artist.getBiography()));
            }
            if (artist.getThumbnailUrl() != null) {
                result.append(String.format("\nThumbnail: %s\n", artist.getThumbnailUrl()));
            }

            return ResponseEntity.ok()
                .header("Content-Type", "text/plain; charset=utf-8")
                .body(result.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/album")
    public ResponseEntity<String> testSearchAlbum(
            @RequestParam("artist") String artist,
            @RequestParam(value = "album", required = false) String album) {
        try {
            String result = toolService.searchAlbumSync(artist, album);
            return ResponseEntity.ok()
                .header("Content-Type", "text/plain; charset=utf-8")
                .body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/help")
    public ResponseEntity<String> help() {
        return ResponseEntity.ok("""
            Available test endpoints:

            GET /test/artist?name={artist_name}
            - Example: /test/artist?name=coldplay
            - Tests the search_artist MCP tool

            GET /test/album?artist={artist_name}&album={album_name}
            - Example: /test/album?artist=coldplay&album=parachutes
            - Example: /test/album?artist=coldplay (all albums)
            - Tests the search_album MCP tool

            These endpoints directly call the MCP tool methods for easy testing.
            The search_track tool is available via MCP but not implemented in test endpoints yet.
            """);
    }
}