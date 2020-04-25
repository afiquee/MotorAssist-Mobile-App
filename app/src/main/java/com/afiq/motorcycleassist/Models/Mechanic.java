package com.afiq.motorcycleassist.Models;

public class Mechanic {

    private String mechId;
    private String mechEmail;
    private String mechName;
    private String mechPhone;
    private String mechStatus;
    private String token;

    public Mechanic() {
    }

    public Mechanic(String mechId, String mechEmail, String mechName, String mechPhone, String mechStatus, String token) {
        this.mechEmail = mechEmail;
        this.mechId = mechId;
        this.mechName = mechName;
        this.mechPhone = mechPhone;
        this.mechStatus = mechStatus;
        this.token = token;
    }

    public String getMechEmail() {
        return mechEmail;
    }

    public String getMechId() {
        return mechId;
    }

    public String getMechName() {
        return mechName;
    }

    public String getMechPhone() {
        return mechPhone;
    }

    public String getMechStatus() {
        return mechStatus;
    }

    public String getToken() {
        return token;
    }

    public void setMechEmail(String mechEmail) {
        this.mechEmail = mechEmail;
    }

    public void setMechId(String mechId) {
        this.mechId = mechId;
    }

    public void setMechName(String mechName) {
        this.mechName = mechName;
    }

    public void setMechPhone(String mechPhone) {
        this.mechPhone = mechPhone;
    }

    public void setMechStatus(String mechStatus) {
        this.mechStatus = mechStatus;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
