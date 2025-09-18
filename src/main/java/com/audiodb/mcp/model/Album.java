package com.audiodb.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Album(
    @JsonProperty("strAlbum")
    String strAlbum,

    @JsonProperty("intYearReleased")
    String intYearReleased,

    @JsonProperty("strArtist")
    String strArtist,

    @JsonProperty("idArtist")
    String idArtist,

    @JsonProperty("idAlbum")
    String idAlbum,

    @JsonProperty("strAlbumThumb")
    String strAlbumThumb,

    @JsonProperty("strAlbumCDart")
    String strAlbumCDart,

    @JsonProperty("strAlbumSpine")
    String strAlbumSpine,

    @JsonProperty("strAlbum3DCase")
    String strAlbum3DCase,

    @JsonProperty("strAlbum3DFlat")
    String strAlbum3DFlat,

    @JsonProperty("strAlbum3DFace")
    String strAlbum3DFace,

    @JsonProperty("strAlbum3DThumb")
    String strAlbum3DThumb,

    @JsonProperty("strDescriptionEN")
    String strDescriptionEN,

    @JsonProperty("strGenre")
    String strGenre,

    @JsonProperty("strStyle")
    String strStyle,

    @JsonProperty("strMood")
    String strMood,

    @JsonProperty("strTheme")
    String strTheme,

    @JsonProperty("strSpeed")
    String strSpeed,

    @JsonProperty("strLabel")
    String strLabel,

    @JsonProperty("strReleaseFormat")
    String strReleaseFormat,

    @JsonProperty("intSales")
    String intSales,

    @JsonProperty("strMusicBrainzID")
    String strMusicBrainzID,

    @JsonProperty("strMusicBrainzArtistID")
    String strMusicBrainzArtistID,

    @JsonProperty("strAllMusicID")
    String strAllMusicID,

    @JsonProperty("strBBCReviewID")
    String strBBCReviewID,

    @JsonProperty("strRateYourMusicID")
    String strRateYourMusicID,

    @JsonProperty("strDiscogsID")
    String strDiscogsID,

    @JsonProperty("strWikidataID")
    String strWikidataID,

    @JsonProperty("strWikipediaID")
    String strWikipediaID,

    @JsonProperty("strGeniusID")
    String strGeniusID,

    @JsonProperty("strLyricFind")
    String strLyricFind,

    @JsonProperty("strMusicMozID")
    String strMusicMozID,

    @JsonProperty("strItunesID")
    String strItunesID,

    @JsonProperty("strAmazonID")
    String strAmazonID,

    @JsonProperty("strLocked")
    String strLocked
) {
    public String getName() {
        return strAlbum;
    }

    public String getArtist() {
        return strArtist;
    }

    public String getReleaseYear() {
        return intYearReleased;
    }

    public String getDescription() {
        return strDescriptionEN;
    }

    public String getGenre() {
        return strGenre;
    }

    public String getStyle() {
        return strStyle;
    }

    public String getLabel() {
        return strLabel;
    }

    public String getThumbnailUrl() {
        return strAlbumThumb;
    }
}