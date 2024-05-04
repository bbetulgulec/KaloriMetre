package com.betulgulec.app;

import java.util.Map;

public class User {
    private String ad;
    private String soyad;
    private String mail;
    private String telefon;
    private String password;
    private String gender;
    private float weight;
    private float height;
    private int age;
    private int targetCalories;
    private Map<String, Object> dailydata; // Günlük veri
    private Map<String, Object> weeklycalories; // Haftalık kaloriler

    // Boş kurucu metod
    public User() {
        // Boş kurucu metod gerekli Firebase işlemleri için
    }

    // Tüm bilgileri içeren kurucu metod
    public User(String ad, String soyad, String mail, String telefon, String password) {
        this.ad = ad;
        this.soyad = soyad;
        this.mail = mail;
        this.telefon = telefon;
        this.password = password;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(int targetCalories) {
        this.targetCalories = targetCalories;
    }

    // dailydata alanı için getter
    public Map<String, Object> getDailydata() {
        return dailydata;
    }

    // dailydata alanı için setter
    public void setDailydata(Map<String, Object> dailydata) {
        this.dailydata = dailydata;
    }

    // weeklycalories alanı için getter
    public Map<String, Object> getWeeklycalories() {
        return weeklycalories;
    }

    // weeklycalories alanı için setter
    public void setWeeklycalories(Map<String, Object> weeklycalories) {
        this.weeklycalories = weeklycalories;
    }
}
