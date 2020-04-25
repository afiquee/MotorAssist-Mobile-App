package com.afiq.motorcycleassist.Models;

public class LatLong {

    private Double latitude;
    private Double longitude;

    public LatLong() {
    }

    public LatLong(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
