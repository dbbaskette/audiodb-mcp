package com.audiodb.mcp.service;

import com.audiodb.mcp.model.Artist;
import com.audiodb.mcp.model.Album;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AudioDbToolService {

    private static final Logger logger = LoggerFactory.getLogger(AudioDbToolService.class);

    private final TheAudioDbClient audioDbClient;

    public AudioDbToolService(TheAudioDbClient audioDbClient) {
        this.audioDbClient = audioDbClient;
    }

    @McpTool(name = "search_artist", description = "Search for a musical artist by name and return comprehensive information including biography (with career history, album sales, awards, achievements), genre, style, formation year, band members, record labels, and images")
    public String searchArtist(
            @McpToolParam(description = "The name of the musical artist to search for", required = true) String artistName) {

        logger.info("MCP Tool called: search_artist with artistName='{}'", artistName);

        try {
            Artist artist = audioDbClient.findArtist(artistName).block();

            if (artist == null || artist.getName() == null) {
                return String.format("No artist found for search term: '%s'", artistName);
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

            if (artist.strMood() != null) {
                result.append(String.format("Mood: %s\n", artist.strMood()));
            }

            if (artist.getBiography() != null && !artist.getBiography().trim().isEmpty()) {
                result.append(String.format("\nBiography:\n%s\n", artist.getBiography()));
            }

            if (artist.getThumbnailUrl() != null) {
                result.append(String.format("\nThumbnail: %s\n", artist.getThumbnailUrl()));
            }

            if (artist.getLogoUrl() != null) {
                result.append(String.format("Logo: %s\n", artist.getLogoUrl()));
            }

            if (artist.strMusicBrainzID() != null) {
                result.append(String.format("MusicBrainz ID: %s\n", artist.strMusicBrainzID()));
            }

            logger.info("Successfully found artist: {}", artist.getName());
            return result.toString();

        } catch (Exception e) {
            logger.error("Error searching for artist '{}': {}", artistName, e.getMessage(), e);
            return String.format("Error searching for artist '%s': %s", artistName, e.getMessage());
        }
    }

    @McpTool(name = "search_album", description = "Search for album information by artist name and optionally album name, returning album details, release info, and descriptions")
    public String searchAlbum(
            @McpToolParam(description = "The name of the musical artist", required = true) String artistName,
            @McpToolParam(description = "The name of the album (optional - if not provided, returns all albums by artist)", required = false) String albumName) {

        logger.info("MCP Tool called: search_album with artistName='{}', albumName='{}'", artistName, albumName);

        try {
            List<Album> albums = audioDbClient.getArtistAlbumsList(artistName).block();

            if (albums == null || albums.isEmpty()) {
                return String.format("No albums found for artist: '%s'", artistName);
            }

            // Filter albums by name if provided
            List<Album> filteredAlbums = albums.stream()
                    .filter(album -> album.getName() != null && !album.getName().trim().isEmpty())
                    .filter(album -> albumName == null || albumName.trim().isEmpty() ||
                           album.getName().toLowerCase().contains(albumName.toLowerCase()))
                    .collect(Collectors.toList());

            if (filteredAlbums.isEmpty()) {
                return albumName != null ?
                    String.format("No albums found matching '%s' by artist '%s'", albumName, artistName) :
                    String.format("No valid albums found for artist: '%s'", artistName);
            }

            StringBuilder result = new StringBuilder();
            if (albumName != null && !albumName.trim().isEmpty()) {
                result.append(String.format("Album search results for '%s' by %s:\n", albumName, artistName));
            } else {
                result.append(String.format("Albums by %s:\n", artistName));
            }
            result.append("============================\n");
            result.append(String.format("Found %d album(s):\n\n", filteredAlbums.size()));

            for (int i = 0; i < filteredAlbums.size(); i++) {
                Album album = filteredAlbums.get(i);
                result.append(String.format("%d. %s", i + 1, album.getName()));

                if (album.getReleaseYear() != null && !album.getReleaseYear().trim().isEmpty()) {
                    result.append(String.format(" (%s)", album.getReleaseYear()));
                }

                if (album.getGenre() != null && !album.getGenre().trim().isEmpty()) {
                    result.append(String.format(" - %s", album.getGenre()));
                }

                if (album.getLabel() != null && !album.getLabel().trim().isEmpty()) {
                    result.append(String.format(" [%s]", album.getLabel()));
                }

                result.append("\n");

                if (album.getDescription() != null && !album.getDescription().trim().isEmpty()) {
                    result.append(String.format("   Description: %s\n", album.getDescription()));
                }

                if (album.getThumbnailUrl() != null && !album.getThumbnailUrl().trim().isEmpty()) {
                    result.append(String.format("   Cover: %s\n", album.getThumbnailUrl()));
                }

                result.append("\n");
            }

            logger.info("Successfully found {} albums for artist: {}", filteredAlbums.size(), artistName);
            return result.toString();

        } catch (Exception e) {
            logger.error("Error searching albums for artist '{}': {}", artistName, e.getMessage(), e);
            return String.format("Error searching albums for artist '%s': %s", artistName, e.getMessage());
        }
    }

    @McpTool(name = "search_track", description = "Search for track/song information by artist name and track name, returning song details and metadata")
    public String searchTrack(
            @McpToolParam(description = "The name of the musical artist", required = true) String artistName,
            @McpToolParam(description = "The name of the track/song", required = true) String trackName) {

        logger.info("MCP Tool called: search_track with artistName='{}', trackName='{}'", artistName, trackName);

        try {
            // Note: AudioDB API doesn't have a direct track search in our current client
            // This is a placeholder that could be implemented with the searchtrack.php endpoint
            // For now, we'll return a helpful message
            return String.format("Track search feature for '%s' by %s is not yet implemented. " +
                "This would use the AudioDB searchtrack.php endpoint: " +
                "https://www.theaudiodb.com/api/v1/json/2/searchtrack.php?s=%s&t=%s",
                trackName, artistName,
                artistName.replace(" ", "_").toLowerCase(),
                trackName.replace(" ", "_").toLowerCase());

        } catch (Exception e) {
            logger.error("Error searching track '{}' by artist '{}': {}", trackName, artistName, e.getMessage(), e);
            return String.format("Error searching track '%s' by artist '%s': %s", trackName, artistName, e.getMessage());
        }
    }

    // Non-reactive methods for direct testing
    public String searchArtistSync(String artistName) {
        try {
            Artist artist = audioDbClient.findArtist(artistName).block();
            if (artist == null || artist.getName() == null) {
                return String.format("No artist found for search term: '%s'", artistName);
            }
            return formatArtistInfo(artist);
        } catch (Exception e) {
            logger.error("Error in sync search for artist '{}': {}", artistName, e.getMessage(), e);
            return String.format("Error searching for artist '%s': %s", artistName, e.getMessage());
        }
    }

    public String searchAlbumSync(String artistName, String albumName) {
        try {
            List<Album> albums = audioDbClient.getArtistAlbumsList(artistName).block();
            if (albums == null || albums.isEmpty()) {
                return String.format("No albums found for artist: '%s'", artistName);
            }
            return formatAlbumSearch(artistName, albumName, albums);
        } catch (Exception e) {
            logger.error("Error in sync album search for artist '{}': {}", artistName, e.getMessage(), e);
            return String.format("Error searching albums for artist '%s': %s", artistName, e.getMessage());
        }
    }

    private String formatArtistInfo(Artist artist) {
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
        if (artist.strMood() != null) {
            result.append(String.format("Mood: %s\n", artist.strMood()));
        }
        if (artist.getBiography() != null && !artist.getBiography().trim().isEmpty()) {
            result.append(String.format("\nBiography:\n%s\n", artist.getBiography()));
        }
        if (artist.getThumbnailUrl() != null) {
            result.append(String.format("\nThumbnail: %s\n", artist.getThumbnailUrl()));
        }
        if (artist.getLogoUrl() != null) {
            result.append(String.format("Logo: %s\n", artist.getLogoUrl()));
        }
        if (artist.strMusicBrainzID() != null) {
            result.append(String.format("MusicBrainz ID: %s\n", artist.strMusicBrainzID()));
        }
        return result.toString();
    }

    private String formatAlbumSearch(String artistName, String albumName, List<Album> albums) {
        List<Album> filteredAlbums = albums.stream()
                .filter(album -> album.getName() != null && !album.getName().trim().isEmpty())
                .filter(album -> albumName == null || albumName.trim().isEmpty() ||
                       album.getName().toLowerCase().contains(albumName.toLowerCase()))
                .collect(Collectors.toList());

        if (filteredAlbums.isEmpty()) {
            return albumName != null ?
                String.format("No albums found matching '%s' by artist '%s'", albumName, artistName) :
                String.format("No valid albums found for artist: '%s'", artistName);
        }

        StringBuilder result = new StringBuilder();
        if (albumName != null && !albumName.trim().isEmpty()) {
            result.append(String.format("Album search results for '%s' by %s:\n", albumName, artistName));
        } else {
            result.append(String.format("Albums by %s:\n", artistName));
        }
        result.append("============================\n");
        result.append(String.format("Found %d album(s):\n\n", filteredAlbums.size()));

        for (int i = 0; i < filteredAlbums.size(); i++) {
            Album album = filteredAlbums.get(i);
            result.append(String.format("%d. %s", i + 1, album.getName()));

            if (album.getReleaseYear() != null && !album.getReleaseYear().trim().isEmpty()) {
                result.append(String.format(" (%s)", album.getReleaseYear()));
            }
            if (album.getGenre() != null && !album.getGenre().trim().isEmpty()) {
                result.append(String.format(" - %s", album.getGenre()));
            }
            if (album.getLabel() != null && !album.getLabel().trim().isEmpty()) {
                result.append(String.format(" [%s]", album.getLabel()));
            }
            result.append("\n");

            if (album.getDescription() != null && !album.getDescription().trim().isEmpty()) {
                result.append(String.format("   Description: %s\n", album.getDescription()));
            }
            if (album.getThumbnailUrl() != null && !album.getThumbnailUrl().trim().isEmpty()) {
                result.append(String.format("   Cover: %s\n", album.getThumbnailUrl()));
            }
            result.append("\n");
        }
        return result.toString();
    }
}