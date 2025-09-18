package com.audiodb.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ArtistSearchResponse(
    @JsonProperty("artists")
    List<Artist> artists
) {
    public List<Artist> getArtists() {
        return artists != null ? artists : List.of();
    }
}