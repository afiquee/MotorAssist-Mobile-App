package com.afiq.motorcycleassist.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Motorcycle implements Parcelable {
    private String motorId;
    private String motorType;
    private String motorBrand;
    private String motorModel;

    public Motorcycle(){
    }

    public Motorcycle(String motorId, String motorType, String motorBrand, String motorModel) {
        this.motorId = motorId;
        this.motorType = motorType;
        this.motorBrand = motorBrand;
        this.motorModel = motorModel;
    }

    protected Motorcycle(Parcel in) {
        motorId = in.readString();
        motorType = in.readString();
        motorBrand = in.readString();
        motorModel = in.readString();
    }

    public static final Creator<Motorcycle> CREATOR = new Creator<Motorcycle>() {
        @Override
        public Motorcycle createFromParcel(Parcel in) {
            return new Motorcycle(in);
        }

        @Override
        public Motorcycle[] newArray(int size) {
            return new Motorcycle[size];
        }
    };

    public String getMotorId() {
        return motorId;
    }

    public String getMotorType() {
        return motorType;
    }

    public String getMotorBrand() {
        return motorBrand;
    }

    public String getmotorModel() {
        return motorModel;
    }


    public void setMotorId(String motorId) {
        this.motorId = motorId;
    }

    public void setMotorType(String motorType) {
        this.motorType = motorType;
    }

    public void setMotorBrand(String motorBrand) {
        this.motorBrand = motorBrand;
    }

    public void setMotorModel(String motorModel) {
        this.motorModel = motorModel;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(motorId);
        dest.writeString(motorType);
        dest.writeString(motorBrand);
        dest.writeString(motorModel);
    }
}
