package com.emrehmrc.harcamalarm.utils;

public class SingletonCurrentValues {
    private int year;
    private int month;
    private int day;
    private String textDate;

    public void setTextDate(String textDate) {
        this.textDate = textDate;
    }

    private static SingletonCurrentValues singletonCurrentValues=null;

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String getTextDate() {
        return textDate;
    }

    private SingletonCurrentValues() {

    }

    public static SingletonCurrentValues getInstance(){
        if(singletonCurrentValues==null){
            singletonCurrentValues=new SingletonCurrentValues();
        }
        return singletonCurrentValues;
    }
}
