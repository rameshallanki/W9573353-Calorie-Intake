package com.ai_factory.calorieapp.model;

import org.checkerframework.checker.units.qual.C;

public class Food {
    private String date;
    private double Calories;
    private String name;
    private String type;

    public Food(){}
    public Food(String name, String date, String type, double Calories){
        this.name=name;
        this.date=date;
        this.Calories= Calories;
        this.type=type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getCalories() {
        return Calories;
    }

    public void setCalories(double calories) {
        Calories = calories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
