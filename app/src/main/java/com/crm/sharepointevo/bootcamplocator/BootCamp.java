package com.crm.sharepointevo.bootcamplocator;

/**
 * Created by ksfgh on 13/08/2017.
 */

public class BootCamp {
    public double longitude;
    public double latitude;
    public String title;
    public String subtitle;

    public BootCamp(double longitude, double latitude, String title, String subtitle) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.title = title;
        this.subtitle = subtitle;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
