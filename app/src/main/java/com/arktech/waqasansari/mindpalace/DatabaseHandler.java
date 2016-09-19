package com.arktech.waqasansari.mindpalace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WaqasAhmed on 5/1/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MindPalace";

    private static final String TABLE_NAME = "memoryDetail";

    //Column names
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_DATE = "date_time";
    private static final String KEY_TAGS = "tags";
    private static final String KEY_IMAGES = "images";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE =
                        "CREATE TABLE " + TABLE_NAME +
                        "(" + KEY_ID + " INTEGER PRIMARY KEY, " +
                        KEY_TYPE + " TEXT, " +
                        KEY_TITLE + " TEXT, " +
                        KEY_DESCRIPTION + " TEXT, " +
                        KEY_DATE + " TEXT, " +
                        KEY_TAGS + " TEXT, " +
                        KEY_IMAGES + " TEXT);";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }


    /*
    **    CRUD OPERATIONS
    */
    public void updateDetail(ClassMemoryDetail classMemoryDetail){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TYPE, classMemoryDetail.getType());
        values.put(KEY_TITLE, classMemoryDetail.getTitle());
        values.put(KEY_DESCRIPTION, classMemoryDetail.getDescription());
        values.put(KEY_DATE, classMemoryDetail.getDated());
        values.put(KEY_TAGS, classMemoryDetail.getTags());
        values.put(KEY_IMAGES, classMemoryDetail.getImage());

        db.update(TABLE_NAME, values, KEY_ID + "=" + classMemoryDetail.getId(), null);
        db.close();
    }

    public void addDetail(ClassMemoryDetail classMemoryDetail){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TYPE, classMemoryDetail.getType());
        values.put(KEY_TITLE, classMemoryDetail.getTitle());
        values.put(KEY_DESCRIPTION, classMemoryDetail.getDescription());
        values.put(KEY_DATE, classMemoryDetail.getDated());
        values.put(KEY_TAGS, classMemoryDetail.getTags());
        values.put(KEY_IMAGES, classMemoryDetail.getImage());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<ClassMemoryDetail> getAllClassDetailMemory() {
        List<ClassMemoryDetail> memoryList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();

        if (cursor.getCount() > 0 && cursor != null) {
            do {
                ClassMemoryDetail memory = new ClassMemoryDetail();
                memory.setId(Integer.parseInt(cursor.getString(0)));
                memory.setType(cursor.getString(1));
                memory.setTitle(cursor.getString(2));
                memory.setDescription(cursor.getString(3));
                memory.setDated(cursor.getString(4));
                memory.setTags(cursor.getString(5));
                memory.setImage(cursor.getString(6));
                // Adding contact to list
                memoryList.add(memory);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return memoryList;
    }

    public int getMemoryCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        db.close();
        return count;
    }

    public void deleteMemory(ClassMemoryDetail memory) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[]{String.valueOf(memory.getId())});
        db.close();
    }

    public List<ClassMemoryDetail> searchEverywhere(String text){
        List<ClassMemoryDetail> memoryList = new ArrayList<>();
        if(text.length() != 0)
            text = "%" + text + "%";
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_TITLE + " LIKE '" + text + "' OR " +
                KEY_TAGS + " LIKE '" + text + "' OR " +
                KEY_DESCRIPTION + " LIKE '" + text + "' OR " +
                KEY_DATE + " LIKE '" + text + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                ClassMemoryDetail memory = new ClassMemoryDetail();
                memory.setId(Integer.parseInt(cursor.getString(0)));
                memory.setType(cursor.getString(1));
                memory.setTitle(cursor.getString(2));
                memory.setDescription(cursor.getString(3));
                memory.setDated(cursor.getString(4));
                memory.setTags(cursor.getString(5));
                memory.setImage(cursor.getString(6));
                // Adding memory to list
                memoryList.add(memory);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return memoryList;
    }
}
