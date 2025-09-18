package com.audiodb.mcp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Artist(
    @JsonProperty("idArtist")
    String idArtist,

    @JsonProperty("strArtist")
    String strArtist,

    @JsonProperty("strArtistStripped")
    String strArtistStripped,

    @JsonProperty("strArtistAlternate")
    String strArtistAlternate,

    @JsonProperty("strLabel")
    String strLabel,

    @JsonProperty("idLabel")
    String idLabel,

    @JsonProperty("intFormedYear")
    String intFormedYear,

    @JsonProperty("intBornYear")
    String intBornYear,

    @JsonProperty("intDiedYear")
    String intDiedYear,

    @JsonProperty("strDisbanded")
    String strDisbanded,

    @JsonProperty("strStyle")
    String strStyle,

    @JsonProperty("strGenre")
    String strGenre,

    @JsonProperty("strMood")
    String strMood,

    @JsonProperty("strWebsite")
    String strWebsite,

    @JsonProperty("strFacebook")
    String strFacebook,

    @JsonProperty("strTwitter")
    String strTwitter,

    @JsonProperty("strBiographyEN")
    String strBiographyEN,

    @JsonProperty("strBiographyDE")
    String strBiographyDE,

    @JsonProperty("strBiographyFR")
    String strBiographyFR,

    @JsonProperty("strBiographyCN")
    String strBiographyCN,

    @JsonProperty("strBiographyIT")
    String strBiographyIT,

    @JsonProperty("strBiographyJP")
    String strBiographyJP,

    @JsonProperty("strBiographyRU")
    String strBiographyRU,

    @JsonProperty("strBiographyES")
    String strBiographyES,

    @JsonProperty("strBiographyPT")
    String strBiographyPT,

    @JsonProperty("strBiographySE")
    String strBiographySE,

    @JsonProperty("strBiographyNL")
    String strBiographyNL,

    @JsonProperty("strBiographyHU")
    String strBiographyHU,

    @JsonProperty("strBiographyNO")
    String strBiographyNO,

    @JsonProperty("strBiographyIL")
    String strBiographyIL,

    @JsonProperty("strBiographyPL")
    String strBiographyPL,

    @JsonProperty("strArtistThumb")
    String strArtistThumb,

    @JsonProperty("strArtistLogo")
    String strArtistLogo,

    @JsonProperty("strArtistCutout")
    String strArtistCutout,

    @JsonProperty("strArtistClearart")
    String strArtistClearart,

    @JsonProperty("strArtistWideThumb")
    String strArtistWideThumb,

    @JsonProperty("strArtistFanart")
    String strArtistFanart,

    @JsonProperty("strArtistFanart2")
    String strArtistFanart2,

    @JsonProperty("strArtistFanart3")
    String strArtistFanart3,

    @JsonProperty("strArtistFanart4")
    String strArtistFanart4,

    @JsonProperty("strArtistBanner")
    String strArtistBanner,

    @JsonProperty("strMusicBrainzID")
    String strMusicBrainzID,

    @JsonProperty("strISNIcode")
    String strISNIcode,

    @JsonProperty("strLastFMChart")
    String strLastFMChart,

    @JsonProperty("strLocked")
    String strLocked
) {
    public String getName() {
        return strArtist;
    }

    public String getBiography() {
        return strBiographyEN;
    }

    public String getGenre() {
        return strGenre;
    }

    public String getStyle() {
        return strStyle;
    }

    public String getFormedYear() {
        return intFormedYear;
    }

    public String getThumbnailUrl() {
        return strArtistThumb;
    }

    public String getLogoUrl() {
        return strArtistLogo;
    }
}