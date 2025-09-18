package com.audiodb.mcp.service;

import com.audiodb.mcp.model.Artist;
import com.audiodb.mcp.model.ArtistSearchResponse;
import com.audiodb.mcp.model.Album;
import com.audiodb.mcp.model.DiscographyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;

/**
 * Simple non-reactive client for testing purposes
 */
@Service
public class SimpleAudioDbClient {

    private static final Logger logger = LoggerFactory.getLogger(SimpleAudioDbClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public SimpleAudioDbClient(
            @Value("${audiodb.api.base-url}") String baseUrl,
            @Value("${audiodb.api.api-key}") String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.restTemplate = new RestTemplate();
        logger.info("Simple AudioDB client initialized with base URL: {} and API key: {}",
                   baseUrl, apiKey.equals("2") ? "test key" : "custom key");
    }

    public Artist searchArtist(String artistName) {
        if (artistName == null || artistName.trim().isEmpty()) {
            logger.warn("Artist name is null or empty");
            return null;
        }

        String url = String.format("%s/%s/search.php?s=%s", baseUrl, apiKey, artistName.trim());
        logger.debug("Searching for artist at URL: {}", url);

        try {
            ArtistSearchResponse response = restTemplate.getForObject(url, ArtistSearchResponse.class);
            if (response != null && response.getArtists() != null && !response.getArtists().isEmpty()) {
                Artist artist = response.getArtists().get(0);
                logger.debug("Found artist: {}", artist.getName());
                return artist;
            }
            logger.debug("No artist found for: {}", artistName);
            return null;
        } catch (RestClientException e) {
            logger.error("Error searching for artist '{}': {}", artistName, e.getMessage(), e);
            return null;
        }
    }

    public List<Album> getArtistDiscography(String artistName) {
        if (artistName == null || artistName.trim().isEmpty()) {
            logger.warn("Artist name is null or empty");
            return Collections.emptyList();
        }

        String url = String.format("%s/%s/discography.php?s=%s", baseUrl, apiKey, artistName.trim());
        logger.debug("Getting discography at URL: {}", url);

        try {
            DiscographyResponse response = restTemplate.getForObject(url, DiscographyResponse.class);
            if (response != null && response.getAlbums() != null) {
                List<Album> albums = response.getAlbums();
                logger.debug("Found {} albums for artist: {}", albums.size(), artistName);
                return albums;
            }
            logger.debug("No albums found for: {}", artistName);
            return Collections.emptyList();
        } catch (RestClientException e) {
            logger.error("Error getting discography for artist '{}': {}", artistName, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}