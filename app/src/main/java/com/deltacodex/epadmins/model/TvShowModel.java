package com.deltacodex.epadmins.model;

import java.io.Serializable;

public class TvShowModel implements Serializable {
    private String id;
    private String name;
    private String thumbnailUrl;
    private String imdb;
    private String rottenTomatoes;
    private String genre;
    private String downloadLink;
    private String trailerLink;

    // Empty Constructor (Firebase Requirement)
    public TvShowModel() {
    }

    public TvShowModel(String id, String name, String thumbnailUrl, String imdb, String rottenTomatoes, String genre, String downloadLink, String trailerLink) {
        this.id = id;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.imdb = imdb;
        this.rottenTomatoes = rottenTomatoes;
        this.genre = genre;
        this.downloadLink = downloadLink;
        this.trailerLink = trailerLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getImdb() {
        return imdb;
    }

    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

    public String getRottenTomatoes() {
        return rottenTomatoes;
    }

    public void setRottenTomatoes(String rottenTomatoes) {
        this.rottenTomatoes = rottenTomatoes;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getTrailerLink() {
        return trailerLink;
    }

    public void setTrailerLink(String trailerLink) {
        this.trailerLink = trailerLink;
    }
}

