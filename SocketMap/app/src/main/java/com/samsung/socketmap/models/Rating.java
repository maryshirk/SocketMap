package com.samsung.socketmap.models;

public class Rating {
    private String placeId;
    private String userId;
    private float grade;

    public Rating(String placeId, String userId, float grade) {
        this.placeId = placeId;
        this.userId = userId;
        this.grade = grade;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}
