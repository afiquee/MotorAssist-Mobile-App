package com.afiq.motorcycleassist.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setRole(String role){
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(role);
        prefsEditor.putString("role", json);
        prefsEditor.commit();
    }

    public void setReceivedRequestId(String requestId){
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(requestId);
        prefsEditor.putString("requestId", json);
        prefsEditor.commit();
    }

    public void setTyre(String tyreType,String tyreSize){
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(tyreType);
        String json2 = gson.toJson(tyreSize);
        prefsEditor.putString("tyreType", json);
        prefsEditor.putString("tyreSize", json2);
        prefsEditor.commit();
    }

    public void setRequest(String request) {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(request);
        prefsEditor.putString("request", json);
        prefsEditor.commit();
    }

    public void setMotorcycle(Motorcycle motorcycle) {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(motorcycle);
        prefsEditor.putString("motorcycle", json);
        prefsEditor.commit();
    }

    public void setService(Service service) {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(service);
        prefsEditor.putString("service", json);
        prefsEditor.commit();
    }

    public Motorcycle getMotorcycle() {

        Gson gson = new Gson();
        String json = prefs.getString("motorcycle", "");
        Motorcycle motorcycle = gson.fromJson(json, Motorcycle.class);
        return motorcycle;
    }

    public String getRole(){
        Gson gson = new Gson();
        String json = prefs.getString("role", "");
        String role = gson.fromJson(json, String.class);
        return role;
    }

    public String getTyreType(){
        Gson gson = new Gson();
        String json = prefs.getString("tyreType", "");
        String tyreType = gson.fromJson(json, String.class);
        return tyreType;
    }

    public String getTyreSize(){
        Gson gson = new Gson();
        String json = prefs.getString("tyreSize", "");
        String tyreSize = gson.fromJson(json, String.class);
        return tyreSize;
    }

    public Service getService() {

        Gson gson = new Gson();
        String json = prefs.getString("service", "");
        Service service = gson.fromJson(json, Service.class);
        return service;
    }

    public String getRequest() {

        Gson gson = new Gson();
        String json = prefs.getString("request", "");
        String request = gson.fromJson(json, String.class);
        return request;
    }

    public String getReceivedRequestId() {

        Gson gson = new Gson();
        String json = prefs.getString("requestId", "");
        String requestId = gson.fromJson(json, String.class);
        return requestId;
    }

    public void clearSession(){
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("tyreSize", null);
        prefsEditor.putString("service", null);
        prefsEditor.putString("request", null);
        prefsEditor.commit();
    }
}
