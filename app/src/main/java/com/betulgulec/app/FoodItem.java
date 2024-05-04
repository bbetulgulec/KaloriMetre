package com.betulgulec.app;

public class FoodItem {
    private String name;
    private String calorie;

    public FoodItem(String name, String calorie) {
        this.name = name;
        this.calorie = calorie;
    }

    public String getName() {
        return name;
    }

    public String getCalorie() {

        return calorie;
    }
}
