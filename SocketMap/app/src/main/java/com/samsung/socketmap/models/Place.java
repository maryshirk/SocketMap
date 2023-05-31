package com.samsung.socketmap.models;

import com.google.android.gms.maps.model.LatLng;

public class Place {
    public String placeId;
    public String address;
    public String description;
    public String userId;
    public double latitude;
    public double longitude;
    private float avgRating;
    private int countRating;

    public Place(String placeId, String address, String description, String userId, double latitude, double longitude) {
        this.placeId = placeId;
        this.address = address;
        this.description = description;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Place() {
        // пустой конструктор без аргументов
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public String getUserId() {
        return userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng getLatLng() {
        return new LatLng(this.latitude, this.longitude);
    }

    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    public int getCountRating() {
        return countRating;
    }

    public void setCountRating(int countRating) {
        this.countRating = countRating;
    }
    public String getName() {
        return address;
    }
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        int earthRadius = 6371; // радиус Земли в км
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;
        return distance;
    }
}
