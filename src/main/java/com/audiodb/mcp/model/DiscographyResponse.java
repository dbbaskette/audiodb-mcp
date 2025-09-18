package com.audiodb.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record DiscographyResponse(
    @JsonProperty("album")
    List<Album> album
) {
    public List<Album> getAlbums() {
        return album != null ? album : List.of();
    }
}