package com.afiq.motorcycleassist.Models;


import java.util.ArrayList;

public class Shop {

    private String shopId;
    private String shopEmail;
    private String shopName;
    private String shopPhone;
    private ArrayList<String> workingDays = new ArrayList<>();
    private String startTime;
    private String endTime;
    private String superbikeTowing;
    private LatLong shopLocation;
    private String shopStatus;
    private String token;


    public Shop() {
    }

    public Shop(String shopId, String shopEmail, String shopName, String shopPhone, ArrayList<String> workingDays, String startTime, String endTime, String superbikeTowing, LatLong shopLocation, String shopStatus, String token) {
        this.shopId = shopId;
        this.shopEmail = shopEmail;
        this.shopName = shopName;
        this.shopPhone = shopPhone;
        this.workingDays = workingDays;
        this.startTime = startTime;
        this.endTime = endTime;
        this.superbikeTowing = superbikeTowing;
        this.shopLocation = shopLocation;
        this.shopStatus = shopStatus;
        this.token = token;
    }

    public String getShopId() {
        return shopId;
    }

    public String getShopEmail() {
        return shopEmail;
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public ArrayList<String> getWorkingDays() {
        return workingDays;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getSuperbikeTowing() {
        return superbikeTowing;
    }

    public LatLong getShopLocation() {
        return shopLocation;
    }

    public String getShopStatus() {
        return shopStatus;
    }

    public String getToken() {
        return token;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public void setShopEmail(String shopEmail) {
        this.shopEmail = shopEmail;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public void setWorkingDays(ArrayList<String> workingDays) {
        this.workingDays = workingDays;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setSuperbikeTowing(String superbikeTowing) {
        this.superbikeTowing = superbikeTowing;
    }

    public void setShopLocation(LatLong shopLocation) {
        this.shopLocation = shopLocation;
    }

    public void setShopStatus(String shopStatus) {
        this.shopStatus = shopStatus;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
