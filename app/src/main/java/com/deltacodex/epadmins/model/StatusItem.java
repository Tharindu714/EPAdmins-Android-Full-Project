package com.deltacodex.epadmins.model;

public class StatusItem {
    private String statusName;
    private int statusIcon;

    public StatusItem(String statusName, int statusIcon) {
        this.statusName = statusName;
        this.statusIcon = statusIcon;
    }

    public String getStatusName() {
        return statusName;
    }

    public int getStatusIcon() {
        return statusIcon;
    }
}
