package com.emrehmrc.harcamalarm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.emrehmrc.harcamalarm.models.ExpenseModel;

import java.util.ArrayList;
import java.util.List;

public class DataBase extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "expense_db";


    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create expense table
        db.execSQL(ExpenseModel.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + ExpenseModel.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public int insertExpense(ExpenseModel e) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(ExpenseModel.COLUMN_YEAR, e.getYear());
        values.put(ExpenseModel.COLUMN_MONTH, e.getMonth());
        values.put(ExpenseModel.COLUMN_DAY, e.getDay());
        values.put(ExpenseModel.COLUMN_AMOUNT, e.getAmount());
        values.put(ExpenseModel.COLUMN_DESCP, e.getDescp());
        values.put(ExpenseModel.COLUMN_IMGID, e.getImgId());

        // insert row
        int id = (int) db.insert(ExpenseModel.TABLE_NAME, null, values);
        // close db connection
        db.close();
        // return newly inserted row id
        return id;
    }

    public ExpenseModel getExpense(int year, int month, int day) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(ExpenseModel.TABLE_NAME,
                new String[]{String.valueOf(ExpenseModel.COLUMN_YEAR),
                        String.valueOf(ExpenseModel.COLUMN_AMOUNT),
                        String.valueOf(ExpenseModel.COLUMN_DAY),
                        ExpenseModel.COLUMN_DESCP,
                        String.valueOf(ExpenseModel.COLUMN_IMGID),
                        String.valueOf(ExpenseModel.COLUMN_MONTH)},
                ExpenseModel.COLUMN_YEAR + "=? and " + ExpenseModel.COLUMN_MONTH + "=? and " +
                        "" + ExpenseModel.COLUMN_DAY + "=?", new
                        String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)},
                null,
                null,
                null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare expense object
        ExpenseModel expenseModel = new ExpenseModel();
        expenseModel.setAmount(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ExpenseModel.COLUMN_AMOUNT))));
        expenseModel.setYear(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ExpenseModel.COLUMN_YEAR))));
        expenseModel.setMonth(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ExpenseModel.COLUMN_MONTH))));
        expenseModel.setDay(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ExpenseModel.COLUMN_DAY))));
        expenseModel.setImgId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ExpenseModel.COLUMN_IMGID))));
        expenseModel.setDescp(cursor.getString(cursor.getColumnIndex(ExpenseModel.COLUMN_DESCP)));
        expenseModel.setId(cursor.getInt(cursor.getColumnIndex(ExpenseModel.COLUMN_ID)));


        // close the db connection
        cursor.close();

        return expenseModel;
    }

    public List<ExpenseModel> getAllExpenseByDate(int year, int month, int day) {
        List<ExpenseModel> expenseModels = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + ExpenseModel.TABLE_NAME + " WHERE year = '" + year
                + "' and month='" + month + "' and day='" + day + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExpenseModel expenseModel = new ExpenseModel();
                expenseModel.setAmount(Integer.parseInt(cursor.getString(cursor.getColumnIndex(String.valueOf(ExpenseModel.COLUMN_AMOUNT)))));
                expenseModel.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(String.valueOf(ExpenseModel.COLUMN_ID)))));
                expenseModel.setYear(Integer.parseInt(cursor.getString(cursor.getColumnIndex(String.valueOf(ExpenseModel.COLUMN_YEAR)))));
                expenseModel.setMonth(Integer.parseInt(cursor.getString(cursor.getColumnIndex(String.valueOf(ExpenseModel.COLUMN_MONTH)))));
                expenseModel.setDay(Integer.parseInt(cursor.getString(cursor.getColumnIndex(String.valueOf(ExpenseModel.COLUMN_DAY)))));
                expenseModel.setImgId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(String.valueOf(ExpenseModel.COLUMN_IMGID)))));
                expenseModel.setDescp(cursor.getString(cursor.getColumnIndex(ExpenseModel.COLUMN_DESCP)));

                expenseModels.add(expenseModel);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return expense list
        return expenseModels;
    }

    public List<ExpenseModel> getAllExpense() {
        List<ExpenseModel> expenseModels = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + ExpenseModel.TABLE_NAME + " ORDER BY " +
                ExpenseModel.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExpenseModel expenseModel = new ExpenseModel();
                expenseModel.setAmount(Integer.parseInt(cursor.getString(cursor.getColumnIndex(String.valueOf(ExpenseModel.COLUMN_AMOUNT)))));
                expenseModel.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(String.valueOf(ExpenseModel.COLUMN_ID)))));
                expenseModel.setYear(Integer.parseInt(cursor.getString(cursor.getColumnIndex(String.valueOf(ExpenseModel.COLUMN_YEAR)))));
                expenseModel.setMonth(Integer.parseInt(cursor.getString(cursor.getColumnIndex(String.valueOf(ExpenseModel.COLUMN_MONTH)))));
                expenseModel.setDay(Integer.parseInt(cursor.getString(cursor.getColumnIndex(String.valueOf(ExpenseModel.COLUMN_DAY)))));
                expenseModel.setImgId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(String.valueOf(ExpenseModel.COLUMN_IMGID)))));
                expenseModel.setDescp(cursor.getString(cursor.getColumnIndex(ExpenseModel.COLUMN_DESCP)));

                expenseModels.add(expenseModel);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return expense list
        return expenseModels;
    }

    public int getExpenseCount(int year, int month, int day) {
        Integer count=0;
        // Select All Query
        String selectQuery = "SELECT * FROM " + ExpenseModel.TABLE_NAME + " WHERE year = '" + year
                + "' and month='" + month + "' and day='" + day + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
             count=count+cursor.getInt(cursor.getColumnIndex((ExpenseModel.COLUMN_AMOUNT)));

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return expense list
        return count;


    }

    public int updateExpense(ExpenseModel e) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(String.valueOf(ExpenseModel.COLUMN_YEAR), e.getYear());
        values.put(String.valueOf(ExpenseModel.COLUMN_AMOUNT), e.getAmount());
        values.put(String.valueOf(ExpenseModel.COLUMN_DAY), e.getDay());
        values.put(ExpenseModel.COLUMN_DESCP, e.getDescp());
        values.put(String.valueOf(ExpenseModel.COLUMN_IMGID), e.getImgId());
        values.put(String.valueOf(ExpenseModel.COLUMN_MONTH), e.getMonth());

        // updating row
        return db.update(ExpenseModel.TABLE_NAME, values, ExpenseModel.COLUMN_ID + " = ?",
                new String[]{String.valueOf(e.getId())});
    }

    public void deleteExpense(ExpenseModel expenseModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ExpenseModel.TABLE_NAME, ExpenseModel.COLUMN_ID + " = ?",
                new String[]{String.valueOf(expenseModel.getId())});
        db.close();
    }
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ExpenseModel.TABLE_NAME, ExpenseModel.COLUMN_ID + " > ?",
                new String[]{String.valueOf(0)});
        db.close();
    }
}