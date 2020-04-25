package com.afiq.motorcycleassist.Models;

public class Motorcyclist {

    private String motoryclistId;
    private String motorcyclistEmail;
    private String motorcyclistName;
    private String motorcyclistPhone;
    private String token;
    private LatLong motorcyclistLocation;

    public Motorcyclist() {
    }

    public Motorcyclist(String motoryclistId, String motorcyclistEmail, String motorcyclistName, String motorcyclistPhone, String token) {
        this.motoryclistId = motoryclistId;
        this.motorcyclistEmail = motorcyclistEmail;
        this.motorcyclistName = motorcyclistName;
        this.motorcyclistPhone = motorcyclistPhone;
        this.token = token;
    }

    public Motorcyclist(String motoryclistId, String motorcyclistEmail, String motorcyclistName, String motorcyclistPhone, String token, LatLong motorcyclistLocation) {
        this.motoryclistId = motoryclistId;
        this.motorcyclistEmail = motorcyclistEmail;
        this.motorcyclistName = motorcyclistName;
        this.motorcyclistPhone = motorcyclistPhone;
        this.token = token;
        this.motorcyclistLocation = motorcyclistLocation;
    }

    public Motorcyclist(String motorcyclistEmail, String motorcyclistName, String motorcyclistPhone, String token) {
        this.motorcyclistEmail = motorcyclistEmail;
        this.motorcyclistName = motorcyclistName;
        this.motorcyclistPhone = motorcyclistPhone;

        this.token = token;
    }

    public String getMotoryclistId() {
        return motoryclistId;
    }

    public String getMotorcyclistEmail() {
        return motorcyclistEmail;
    }

    public String getMotorcyclistName() {
        return motorcyclistName;
    }

    public String getMotorcyclistPhone() {
        return motorcyclistPhone;
    }

    public String getToken() {
        return token;
    }

    public void setMotoryclistId(String motoryclistId) {
        this.motoryclistId = motoryclistId;
    }

    public void setMotorcyclistEmail(String motorcyclistEmail) {
        this.motorcyclistEmail = motorcyclistEmail;
    }

    public void setMotorcyclistName(String motorcyclistName) {
        this.motorcyclistName = motorcyclistName;
    }

    public void setMotorcyclistPhone(String motorcyclistPhone) {
        this.motorcyclistPhone = motorcyclistPhone;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
