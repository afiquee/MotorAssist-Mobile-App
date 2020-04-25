package com.afiq.motorcycleassist.Models;

public class MotorType {

    private String typeId;
    private String typeName;

    public MotorType() {
    }

    public MotorType(String typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    public String getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public String toString(){
        return typeName;
    }
}
