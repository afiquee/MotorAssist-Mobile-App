package com.afiq.motorcycleassist.Models;

public class Request {

    private String requestId;
    private String serviceName;
    private String additionalInfo;
    private double motorcyclistDistance;
    private double shopDistance;
    private String startTime;
    private String endTime;
    private double travelingCharge;
    private double minServiceCharge;
    private double maxServiceCharge;
    private double finalServiceCharge;
    private double minTotal;
    private double maxTotal;
    private double finalTotal;
    private String status;
    private String motorcyclistId;
    private String motorcycleId;
    private String mechId;
    private String shopId;
    private LatLong requestLocation;

    public Request() {
    }

    public Request(String requestId, String serviceName, String additionalInfo, double motorcyclistDistance, double shopDistance, String startTime, String endTime, double travelingCharge, double minServiceCharge, double maxServiceCharge, double finalServiceCharge, double minTotal, double maxTotal, double finalTotal, String status, String motorcyclistId, String motorcycleId, String mechId, String shopId, com.afiq.motorcycleassist.Models.LatLong requestLocation) {
        this.requestId = requestId;
        this.serviceName = serviceName;
        this.additionalInfo = additionalInfo;
        this.motorcyclistDistance = motorcyclistDistance;
        this.shopDistance = shopDistance;
        this.startTime = startTime;
        this.endTime = endTime;
        this.travelingCharge = travelingCharge;
        this.minServiceCharge = minServiceCharge;
        this.maxServiceCharge = maxServiceCharge;
        this.finalServiceCharge = finalServiceCharge;
        this.minTotal = minTotal;
        this.maxTotal = maxTotal;
        this.finalTotal = finalTotal;
        this.status = status;
        this.motorcyclistId = motorcyclistId;
        this.motorcycleId = motorcycleId;
        this.mechId = mechId;
        this.shopId = shopId;
        this.requestLocation = requestLocation;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getAdditionalInfo(){
        return additionalInfo;
    }

    public double getMotorcyclistDistance() {
        return motorcyclistDistance;
    }

    public double getShopDistance() {
        return shopDistance;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public double getTravelingCharge() {
        return travelingCharge;
    }

    public double getMinServiceCharge() {
        return minServiceCharge;
    }

    public double getMaxServiceCharge() {
        return maxServiceCharge;
    }

    public double getFinalServiceCharge() {
        return finalServiceCharge;
    }

    public double getMinTotal() {
        return minTotal;
    }

    public double getMaxTotal() {
        return maxTotal;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public String getStatus() {
        return status;
    }

    public String getMotorcyclistId() {
        return motorcyclistId;
    }

    public String getMotorcycleId() {
        return motorcycleId;
    }

    public String getMechId() {
        return mechId;
    }

    public String getShopId() {
        return shopId;
    }

    public LatLong getRequestLocation() {
        return requestLocation;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setMotorcyclistDistance(double motorcyclistDistance) {
        this.motorcyclistDistance = motorcyclistDistance;
    }

    public void setShopDistance(double shopDistance) {
        this.shopDistance = shopDistance;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setTravelingCharge(double travelingCharge) {
        this.travelingCharge = travelingCharge;
    }

    public void setMinServiceCharge(double minServiceCharge) {
        this.minServiceCharge = minServiceCharge;
    }

    public void setMaxServiceCharge(double maxServiceCharge) {
        this.maxServiceCharge = maxServiceCharge;
    }

    public void setFinalServiceCharge(double finalServiceCharge) {
        this.finalServiceCharge = finalServiceCharge;
    }

    public void setMinTotal(double minTotal) {
        this.minTotal = minTotal;
    }

    public void setMaxTotal(double maxTotal) {
        this.maxTotal = maxTotal;
    }

    public void setFinalTotal(double finalTotal) {
        this.finalTotal = finalTotal;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMotorcyclistId(String motorcyclistId) {
        this.motorcyclistId = motorcyclistId;
    }

    public void setMotorcycleId(String motorcycleId) {
        this.motorcycleId = motorcycleId;
    }

    public void setMechId(String mechanicId) {
        this.mechId = mechanicId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public void setRequestLocation(com.afiq.motorcycleassist.Models.LatLong requestLocation) {
        this.requestLocation = requestLocation;
    }
}
