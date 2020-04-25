package com.afiq.motorcycleassist.Models;

public class GoodSamaritanRequest {

    private String gsrId;
    private String gsrName;
    private String gsrRequesterId;
    private String gsrMotorcycleId;
    private String gsrRescuerId;
    private double gsrDistance;
    private String gsrDestination;
    private String gsrStatus;
    private String gsrStartTime;
    private String gsrEndTime;
    private LatLong gsrLocation;
    private LatLong gsrDestinationLocation;

    public GoodSamaritanRequest() {

    }

    public GoodSamaritanRequest(String gsrId, String gsrName, String gsrRequesterId, String gsrMotorcycleId, String gsrRescuerId, double gsrDistance, String gsrDestination, String gsrStatus, String gsrStartTime, String gsrEndTime, LatLong gsrLocation, LatLong gsrDestinationLocation) {
        this.gsrId = gsrId;
        this.gsrName = gsrName;
        this.gsrRequesterId = gsrRequesterId;
        this.gsrMotorcycleId = gsrMotorcycleId;
        this.gsrRescuerId = gsrRescuerId;
        this.gsrDistance = gsrDistance;
        this.gsrDestination = gsrDestination;
        this.gsrStatus = gsrStatus;
        this.gsrStartTime = gsrStartTime;
        this.gsrEndTime = gsrEndTime;
        this.gsrLocation =gsrLocation;
        this.gsrDestinationLocation = gsrDestinationLocation;
    }

    public String getGsrId() {
        return gsrId;
    }

    public String getGsrName() {
        return gsrName;
    }

    public String getGsrRequesterId() {
        return gsrRequesterId;
    }

    public String getGsrMotorcycleId() {
        return gsrMotorcycleId;
    }

    public String getGsrRescuerId() {
        return gsrRescuerId;
    }

    public double getGsrDistance() {
        return gsrDistance;
    }

    public String getGsrDestination() {
        return gsrDestination;
    }

    public String getGsrStatus() {
        return gsrStatus;
    }

    public String getGsrStartTime() {
        return gsrStartTime;
    }

    public String getGsrEndTime() {
        return gsrEndTime;
    }

    public LatLong getGsrLocation() {
        return gsrLocation;
    }
    public LatLong getGsrDestinationLocation() {
        return gsrDestinationLocation;
    }

    public void setGsrId(String gsrId) {
        this.gsrId = gsrId;
    }

    public void setGsrName(String gsrName) {
        this.gsrName = gsrName;
    }

    public void setGsrRequesterId(String gsrRequesterId) {
        this.gsrRequesterId = gsrRequesterId;
    }

    public void setGsrMotorcycleId(String gsrMotorcycleId) {
        this.gsrMotorcycleId = gsrMotorcycleId;
    }

    public void setGsrRescuerId(String gsrRescuerId) {
        this.gsrRescuerId = gsrRescuerId;
    }

    public void setGsrDistance(double gsrDistance) {
        this.gsrDistance = gsrDistance;
    }

    public void setGsrDestination(String gsrDestination) {
        this.gsrDestination = gsrDestination;
    }

    public void setGsrStatus(String gsrStatus) {
        this.gsrStatus = gsrStatus;
    }

    public void setGsrStartTime(String gsrStartTime) {
        this.gsrStartTime = gsrStartTime;
    }

    public void setGsrEndTime(String gsrEndTime) {
        this.gsrEndTime = gsrEndTime;
    }

    public void setGsrLocation(LatLong gsrLocation) {
        this.gsrLocation = gsrLocation;
    }

    public void setGsrDestinationLocation(LatLong gsrDestinationLocation) {
        this.gsrDestinationLocation = gsrDestinationLocation;
    }
}
