package com.zain.phonebook10;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SqlLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactsManager";
    private static final String KEY_ID = "contact_id";
    private static final String KEY_USERID = "user_id";
    private static final String KEY_NAME = "contact_name";
    private static final String KEY_PH_NO = "contact_number";
    private static final String KEY_EMAIL = "contact_email";
    private static final String KEY_STATUS = "contact_status";
    private static final String KEY_PIC = "contact_picture";



    public SqlLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE contacts(" +
                "contact_id INTEGER NOT NULL," +
                "user_id INTEGER  NOT NULL," +
                "contact_name TEXT," +
                "contact_number TEXT," +
                "contact_email TEXT," +
                "contact_status INTEGER DEFAULT 0," +
                "contact_picture TEXT)";

        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS contacts");

        // Create tables again
        onCreate(db);
    }


    // code to add the new contact
    @RequiresApi(api = Build.VERSION_CODES.O)
    void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,contact.getContactId());
        values.put(KEY_USERID,contact.getuserId());
        values.put(KEY_NAME, contact.getcontactName()); // Contact Name
        values.put(KEY_PH_NO, contact.getcontactNumber()); // Contact Phone
        values.put(KEY_EMAIL,contact.getcontactEmail());
        values.put(KEY_STATUS,contact.getcontactStatus());
        values.put(KEY_PIC,contact.getContactPicture());


            db.insert("contacts", null, values);


        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, "contacts");
        return numRows;
    }

    // code to get the single contact
    public ArrayList<Contact> getContact(int userId, String search) {
        ArrayList<Contact> array_list = new ArrayList<Contact>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from contacts where user_id = "+userId+" and ("+KEY_NAME+" like '%"+search+"%' OR "+KEY_NAME+" like '%"+search+"%');", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            array_list.add(new Contact(Integer.parseInt(res.getString(res.getColumnIndex(KEY_ID))),
                    Integer.parseInt(res.getString(res.getColumnIndex(KEY_USERID))), res.getString(res.getColumnIndex(KEY_NAME)),
                    res.getString(res.getColumnIndex(KEY_PH_NO)), res.getString(res.getColumnIndex(KEY_EMAIL)),
                    res.getString(res.getColumnIndex(KEY_STATUS)),res.getString(res.getColumnIndex(KEY_PIC))));
            res.moveToNext();
        }
        return array_list;

    }

    public ArrayList<Contact> getAllCotacts(int userId,int limit, int offset) {
        ArrayList<Contact> array_list = new ArrayList<Contact>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from contacts where user_id ="+userId+" LIMIT "+limit+" OFFSET "+offset+";", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {

            array_list.add(new Contact(Integer.parseInt(res.getString(res.getColumnIndex(KEY_ID))),
                    Integer.parseInt(res.getString(res.getColumnIndex(KEY_USERID))), res.getString(res.getColumnIndex(KEY_NAME)),
                    res.getString(res.getColumnIndex(KEY_PH_NO)), res.getString(res.getColumnIndex(KEY_EMAIL)),
                    res.getString(res.getColumnIndex(KEY_STATUS)),res.getString(res.getColumnIndex(KEY_PIC))));
            res.moveToNext();
        }
        return array_list;
    }

    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("contacts", KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getContactId()) });
        db.close();
    }




}
