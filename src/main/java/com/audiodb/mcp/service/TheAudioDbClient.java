package com.audiodb.mcp.service;

import com.audiodb.mcp.model.Artist;
import com.audiodb.mcp.model.ArtistSearchResponse;
import com.audiodb.mcp.model.Album;
import com.audiodb.mcp.model.DiscographyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Service
public class TheAudioDbClient {

    private static final Logger logger = LoggerFactory.getLogger(TheAudioDbClient.class);

    private final WebClient webClient;
    private final String apiKey;

    public TheAudioDbClient(
            @Value("${audiodb.api.base-url}") String baseUrl,
            @Value("${audiodb.api.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();

        logger.info("TheAudioDB client initialized with base URL: {} and API key: {}",
                   baseUrl, apiKey.equals("2") ? "test key" : "custom key");
    }

    /**
     * Search for artists by name
     * @param artistName The name of the artist to search for
     * @return Flux of Artist objects matching the search term
     */
    public Flux<Artist> searchArtist(String artistName) {
        logger.debug("Searching for artist: {}", artistName);

        if (artistName == null || artistName.trim().isEmpty()) {
            logger.warn("Artist name is null or empty");
            return Flux.empty();
        }

        return webClient.get()
                .uri("/{apiKey}/search.php?s={artistName}", apiKey, artistName.trim())
                .retrieve()
                .bodyToMono(ArtistSearchResponse.class)
                .doOnNext(response -> logger.debug("Received response with {} artists",
                         response.getArtists().size()))
                .flatMapMany(response -> Flux.fromIterable(response.getArtists()))
                .doOnError(error -> logger.error("Error searching for artist '{}': {}",
                          artistName, error.getMessage()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                    .maxBackoff(Duration.ofSeconds(5)))
                .onErrorResume(error -> {
                    logger.error("Error in artist search, returning empty result: {}", error.getMessage());
                    return Flux.empty();
                });
    }

    /**
     * Get artist discography (albums) by artist name
     * @param artistName The name of the artist
     * @return Flux of Album objects for the artist
     */
    public Flux<Album> getArtistDiscography(String artistName) {
        logger.debug("Getting discography for artist: {}", artistName);

        if (artistName == null || artistName.trim().isEmpty()) {
            logger.warn("Artist name is null or empty");
            return Flux.empty();
        }

        return webClient.get()
                .uri("/{apiKey}/discography.php?s={artistName}", apiKey, artistName.trim())
                .retrieve()
                .bodyToMono(DiscographyResponse.class)
                .doOnNext(response -> logger.debug("Received discography with {} albums",
                         response.getAlbums().size()))
                .flatMapMany(response -> Flux.fromIterable(response.getAlbums()))
                .doOnError(error -> logger.error("Error getting discography for artist '{}': {}",
                          artistName, error.getMessage()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                    .maxBackoff(Duration.ofSeconds(5)))
                .onErrorResume(error -> {
                    logger.error("Error in discography search, returning empty result: {}", error.getMessage());
                    return Flux.empty();
                });
    }

    /**
     * Search for a single artist by name (returns the first match)
     * @param artistName The name of the artist to search for
     * @return Mono containing the first matching Artist, or empty if none found
     */
    public Mono<Artist> findArtist(String artistName) {
        return searchArtist(artistName)
                .next()
                .doOnNext(artist -> logger.debug("Found artist: {}", artist.getName()));
    }

    /**
     * Get all albums for an artist as a list
     * @param artistName The name of the artist
     * @return Mono containing a List of Albums
     */
    public Mono<List<Album>> getArtistAlbumsList(String artistName) {
        return getArtistDiscography(artistName)
                .collectList()
                .doOnNext(albums -> logger.debug("Collected {} albums for artist: {}",
                         albums.size(), artistName));
    }
}