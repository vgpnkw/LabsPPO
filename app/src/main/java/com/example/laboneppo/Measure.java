package com.example.laboneppo;

public enum Measure {
    METERS(MeasureCategories.DISTANCE, 1.0),
    MILES(MeasureCategories.DISTANCE, 0.00063548213),
    FOOT(MeasureCategories.DISTANCE, 3.28084),
    BYN(MeasureCategories.CURRENCY, 1.0),
    USD(MeasureCategories.CURRENCY, 0.29),
    EUR(MeasureCategories.CURRENCY, 0.3672),
    KILOGRAMM(MeasureCategories.WEIGHT, 1.0),
    CENTNER(MeasureCategories.WEIGHT, 100),
    GRAMM(MeasureCategories.WEIGHT, 1000.0);
    private MeasureCategories category;
    private double coefficient;
    Measure(MeasureCategories category, double coefficient){
        this.category = category;
        this.coefficient = coefficient;
    }

    public MeasureCategories getCategory(){
        return category;
    }

    public double getCoefficient(){
        return coefficient;
    }

}
