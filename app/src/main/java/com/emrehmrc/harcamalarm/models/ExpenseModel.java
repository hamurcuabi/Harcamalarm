package com.emrehmrc.harcamalarm.models;

import com.emrehmrc.harcamalarm.utils.Util;

public class ExpenseModel {
    public static final String TABLE_NAME = "expense";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DESCP = "descp";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_IMGID = "imgId";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_DESCP + " TEXT,"
                    + COLUMN_YEAR + " INTEGER,"
                    + COLUMN_DAY + " INTEGER,"
                    + COLUMN_MONTH + " INTEGER,"
                    + COLUMN_IMGID + " INTEGER,"
                    + COLUMN_AMOUNT + " INTEGER,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    private String descp;
    private Integer imgId;
    private int amount;
    private int year;
    private int day;
    private int month;
    private int id;

    public ExpenseModel(String descp, Integer imgId, int amount, int year, int month, int day) {
        this.descp = descp;
        this.imgId = imgId;
        this.amount = amount;
        this.year = year;
        this.day = day;
        if (month > 1) this.month = month - 1;
        else {
            this.month = 12;
            this.year = year - 1;
        }

    }

    public ExpenseModel() {
    }

    public ExpenseModel(String descp, Integer imgId, Integer amount) {
        this.descp = descp;
        this.imgId = imgId;
        this.amount = amount;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {

        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescp() {

        return descp;
    }

    public void setDescp(String descp) {
        this.descp = descp;
    }

    public Integer getImgId() {
        return imgId;
    }

    public void setImgId(Integer imgId) {
        this.imgId = imgId;
    }

}
