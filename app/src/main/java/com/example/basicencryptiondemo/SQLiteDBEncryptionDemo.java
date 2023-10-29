package com.example.basicencryptiondemo;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
public class SQLiteDBEncryptionDemo extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SQLiteDmoDB";
    public static final String CONTACTS_TABLE_NAME = "SalaryEStore";
    public SQLiteDBEncryptionDemo(Context context) {
        super(context, DATABASE_NAME, null, 2);
}
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(
                    "create table " + CONTACTS_TABLE_NAME + "(id INTEGER PRIMARY KEY, name text,salary float,datetime default current_timestamp)"
            );
        } catch (SQLiteException e) {
            try {
                throw new IOException(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
        onCreate(db);
    }
    public static final String hashingdetails(final String s) {
        final String sha256 = "SHA256";
        try {
            // Create sha256 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(sha256);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean insert(String employeename, String employeesalary) {
        SQLiteDatabase db = this.getWritableDatabase();
        employeesalary = hashingdetails(employeesalary);
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", employeename);
        contentValues.put("salary", employeesalary);
        db.replace(CONTACTS_TABLE_NAME, null, contentValues);
        return true;
    }


    @SuppressLint("Range")
    public ArrayList read() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> array_list = new ArrayList<String>();
        Cursor res = db.rawQuery("select (id ||' : '||name || ' : ' ||salary || ' : '|| datetime) as fullname from " + CONTACTS_TABLE_NAME, null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if ((res != null) && (res.getCount() > 0))
                array_list.add(res.getString(res.getColumnIndex("fullname")));
            res.moveToNext();
        }
        return array_list;
    }

    /*
    public boolean update(String fname, String salary) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + CONTACTS_TABLE_NAME + " SET name = " + "'" + fname + "', " + "salary = " + "'" + salary + "'");
        return true;
    }
    */

    public boolean update(String fname, String salary) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", fname);
        values.put("salary", salary);

        String whereClause = "id = ?"; // Assuming there's a column named "id" for identifying the record
        String[] whereArgs = new String[] { "1" }; // Assuming you want to update the record with id 1

        int rowsUpdated = db.update(CONTACTS_TABLE_NAME, values, whereClause, whereArgs);

        return rowsUpdated > 0;
    }

    public boolean delete() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE from " + CONTACTS_TABLE_NAME);
        return true;
    }
}