package com.afiq.motorcycleassist.Models;

public class Service {

    private String serviceName;
    private int minPrice;
    private int maxPrice;

    public Service() {
    }

    public Service(String serviceName, int minPrice, int maxPrice) {
        this.serviceName = serviceName;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }
}
