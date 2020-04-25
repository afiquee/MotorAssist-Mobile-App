package com.afiq.motorcycleassist.Models;

public class MotorBrand {

    private String brandId;
    private String brandName;

    public MotorBrand() {

    }

    public MotorBrand(String brandId, String brandName) {
        this.brandId = brandId;
        this.brandName = brandName;
    }

    public String getBrandId() {
        return brandId;
    }

    public String getBrandName() {
        return brandName;
    }
    public String toString(){
        return brandName;
    }
}
