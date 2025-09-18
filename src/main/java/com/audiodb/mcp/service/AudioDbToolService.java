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

    @McpTool(name = "search_artist", description = "Search for a musical artist by name and return detailed information about them including biography, genre, formed year, and images")
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

    @McpTool(name = "get_artist_discography", description = "Retrieve the complete discography (list of albums) for a given musical artist including album names, release years, and details")
    public String getArtistDiscography(
            @McpToolParam(description = "The name of the musical artist whose discography to retrieve", required = true) String artistName) {

        logger.info("MCP Tool called: get_artist_discography with artistName='{}'", artistName);

        try {
            List<Album> albums = audioDbClient.getArtistAlbumsList(artistName).block();

            if (albums == null || albums.isEmpty()) {
                return String.format("No albums found for artist: '%s'", artistName);
            }

            // Filter out albums with null names
            List<Album> validAlbums = albums.stream()
                    .filter(album -> album.getName() != null && !album.getName().trim().isEmpty())
                    .collect(Collectors.toList());

            if (validAlbums.isEmpty()) {
                return String.format("No valid albums found for artist: '%s'", artistName);
            }

            StringBuilder result = new StringBuilder();
            result.append(String.format("Discography for %s:\n", artistName));
            result.append("====================\n");
            result.append(String.format("Total Albums: %d\n\n", validAlbums.size()));

            for (int i = 0; i < validAlbums.size(); i++) {
                Album album = validAlbums.get(i);
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

            logger.info("Successfully retrieved {} albums for artist: {}", validAlbums.size(), artistName);
            return result.toString();

        } catch (Exception e) {
            logger.error("Error getting discography for artist '{}': {}", artistName, e.getMessage(), e);
            return String.format("Error getting discography for artist '%s': %s", artistName, e.getMessage());
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

    public String getArtistDiscographySync(String artistName) {
        try {
            List<Album> albums = audioDbClient.getArtistAlbumsList(artistName).block();
            if (albums == null || albums.isEmpty()) {
                return String.format("No albums found for artist: '%s'", artistName);
            }
            return formatDiscography(artistName, albums);
        } catch (Exception e) {
            logger.error("Error in sync discography for artist '{}': {}", artistName, e.getMessage(), e);
            return String.format("Error getting discography for artist '%s': %s", artistName, e.getMessage());
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

    private String formatDiscography(String artistName, List<Album> albums) {
        List<Album> validAlbums = albums.stream()
                .filter(album -> album.getName() != null && !album.getName().trim().isEmpty())
                .collect(Collectors.toList());

        if (validAlbums.isEmpty()) {
            return String.format("No valid albums found for artist: '%s'", artistName);
        }

        StringBuilder result = new StringBuilder();
        result.append(String.format("Discography for %s:\n", artistName));
        result.append("====================\n");
        result.append(String.format("Total Albums: %d\n\n", validAlbums.size()));

        for (int i = 0; i < validAlbums.size(); i++) {
            Album album = validAlbums.get(i);
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