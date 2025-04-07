package com.example.attendancemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "attendance.db";
    private static final int DATABASE_VERSION = 1;


    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";


    private static final String TABLE_ATTENDANCE = "attendance";
    private static final String COLUMN_ATTENDANCE_ID = "id";
    private static final String COLUMN_SUBJECT = "subject";
    private static final String COLUMN_ATTENDED_CLASSES = "attended_classes";
    private static final String COLUMN_TOTAL_CLASSES = "total_classes";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsersTable = "CREATE TABLE " + TABLE_USERS +
                " (" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);


        String createAttendanceTable = "CREATE TABLE " + TABLE_ATTENDANCE +
                " (" + COLUMN_ATTENDANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SUBJECT + " TEXT, " +
                COLUMN_ATTENDED_CLASSES + " INTEGER, " +
                COLUMN_TOTAL_CLASSES + " INTEGER)";
        db.execSQL(createAttendanceTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        onCreate(db);
    }


    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, contentValues);
        return result != -1;
    }

    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USERNAME + " = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                        COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[]{username, password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }

    public boolean insertAttendance(AttendanceModel attendance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SUBJECT, attendance.getSubject());
        contentValues.put(COLUMN_ATTENDED_CLASSES, attendance.getAttendedClasses());
        contentValues.put(COLUMN_TOTAL_CLASSES, attendance.getTotalClasses());
        long result = db.insert(TABLE_ATTENDANCE, null, contentValues);
        return result != -1;
    }


    public boolean updateAttendance(AttendanceModel attendance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SUBJECT, attendance.getSubject());
        contentValues.put(COLUMN_ATTENDED_CLASSES, attendance.getAttendedClasses());
        contentValues.put(COLUMN_TOTAL_CLASSES, attendance.getTotalClasses());


        long result = db.update(TABLE_ATTENDANCE, contentValues,
                COLUMN_ATTENDANCE_ID + " = ?",
                new String[]{String.valueOf(attendance.getId())});

        return result != -1;
    }


    public boolean deleteAttendance(int id) {
        SQLiteDatabase db = this.getWritableDatabase();


        long result = db.delete(TABLE_ATTENDANCE,
                COLUMN_ATTENDANCE_ID + " = ?",
                new String[]{String.valueOf(id)});

        return result != -1;
    }

    public ArrayList<AttendanceModel> getAllAttendance() {
        ArrayList<AttendanceModel> attendanceList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ATTENDANCE, null);

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(COLUMN_ATTENDANCE_ID);
                int subjectIndex = cursor.getColumnIndex(COLUMN_SUBJECT);
                int attendedIndex = cursor.getColumnIndex(COLUMN_ATTENDED_CLASSES);
                int totalIndex = cursor.getColumnIndex(COLUMN_TOTAL_CLASSES);

                do {
                    int id = cursor.getInt(idIndex);
                    String subject = cursor.getString(subjectIndex);
                    int attended = cursor.getInt(attendedIndex);
                    int total = cursor.getInt(totalIndex);

                    AttendanceModel attendance = new AttendanceModel(id, subject, attended, total);
                    attendanceList.add(attendance);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attendanceList;
    }
}