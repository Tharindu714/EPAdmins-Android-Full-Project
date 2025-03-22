package com.deltacodex.epadmins.model;
public class GameModel {
    private String g_id, name, description, Released_Date, genre, Developer, Platforms, downloadLink, trailerLink, thumbnailUrl, LargeImageUrl;

    // Required empty constructor for Firestore
    public GameModel() {}

    public GameModel(String g_id, String name, String description, String Released_Date, String genre,
                String Developer, String Platforms, String downloadLink, String trailerLink,
                String thumbnailUrl, String LargeImageUrl) {
        this.g_id = g_id;
        this.name = name;
        this.description = description;
        this.Released_Date = Released_Date;
        this.genre = genre;
        this.Developer = Developer;
        this.Platforms = Platforms;
        this.downloadLink = downloadLink;
        this.trailerLink = trailerLink;
        this.thumbnailUrl = thumbnailUrl;
        this.LargeImageUrl = LargeImageUrl;
    }

    public String getG_id() {
        return g_id;
    }

    public void setG_id(String g_id) {
        this.g_id = g_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleased_Date() {
        return Released_Date;
    }

    public void setReleased_Date(String released_Date) {
        Released_Date = released_Date;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDeveloper() {
        return Developer;
    }

    public void setDeveloper(String developer) {
        Developer = developer;
    }

    public String getPlatforms() {
        return Platforms;
    }

    public void setPlatforms(String platforms) {
        Platforms = platforms;
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getLargeImageUrl() {
        return LargeImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        LargeImageUrl = largeImageUrl;
    }
}
