package com.deltacodex.epadmins.model;

public class MediaItem {
    private String thumbnailUrl, name, extraInfo, status, id;

    public MediaItem() {}

    public MediaItem(String id, String thumbnailUrl, String name, String extraInfo, String status) {
        this.id = id;
        this.thumbnailUrl = thumbnailUrl;
        this.name = name;
        this.extraInfo = extraInfo;
        this.status = status;
    }

    public String getId() { return id; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getName() { return name; }
    public String getExtraInfo() { return extraInfo; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
