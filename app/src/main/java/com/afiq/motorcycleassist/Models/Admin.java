package com.afiq.motorcycleassist.Models;

public class Admin {

    private String adminId;
    private String adminEmail;
    private String adminName;
    private String token;

    public Admin() {
    }

    public Admin(String adminId, String adminEmail, String adminName, String token) {
        this.adminId = adminId;
        this.adminEmail = adminEmail;
        this.adminName = adminName;
        this.token = token;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getToken() {
        return token;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
