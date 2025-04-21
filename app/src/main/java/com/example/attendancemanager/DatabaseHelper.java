package com.example.attendancemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "attendance.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TAG = "Fragmentnotes";
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_ATTENDANCE = "attendance";
    private static final String COLUMN_ATTENDANCE_ID = "id";
    private static final String COLUMN_SUBJECT = "subject";
    private static final String COLUMN_ATTENDED_CLASSES = "attended_classes";
    private static final String COLUMN_TOTAL_CLASSES = "total_classes";

    // New table for notes
    private static final String TABLE_NOTES = "notes";
    private static final String COLUMN_NOTE_ID = "id";
    private static final String COLUMN_NOTE_TITLE = "title";
    private static final String COLUMN_NOTE_CONTENT = "content";
    private static final String COLUMN_NOTE_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS +
                " (" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);

        // Create Attendance table
        String createAttendanceTable = "CREATE TABLE " + TABLE_ATTENDANCE +
                " (" + COLUMN_ATTENDANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SUBJECT + " TEXT, " +
                COLUMN_ATTENDED_CLASSES + " INTEGER, " +
                COLUMN_TOTAL_CLASSES + " INTEGER)";
        db.execSQL(createAttendanceTable);

        // Create Notes table
        String createNotesTable = "CREATE TABLE " + TABLE_NOTES +
                " (" + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOTE_TITLE + " TEXT, " +
                COLUMN_NOTE_CONTENT + " TEXT, " +
                COLUMN_NOTE_TIMESTAMP + " TEXT)";
        db.execSQL(createNotesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    // User related methods
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
                COLUMN_USERNAME + " = ?", new String[] { username });
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                        COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                new String[] { username, password });
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }

    // Attendance related methods
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
                new String[] { String.valueOf(attendance.getId()) });

        return result != -1;
    }

    public boolean deleteAttendance(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_ATTENDANCE,
                COLUMN_ATTENDANCE_ID + " = ?",
                new String[] { String.valueOf(id) });

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


    public boolean insertNote(NotesModel note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTE_TITLE, note.getTitle());
        contentValues.put(COLUMN_NOTE_CONTENT, note.getContent());


        String timestamp = note.getTimestamp();
        if (timestamp == null || timestamp.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            timestamp = sdf.format(new Date());
        }
        contentValues.put(COLUMN_NOTE_TIMESTAMP, timestamp);
        Log.i(TAG, contentValues.toString());
             long result = db.insert(TABLE_NOTES, null, contentValues);
        return result != -1;
    }

    public boolean updateNote(NotesModel note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTE_TITLE, note.getTitle());
        contentValues.put(COLUMN_NOTE_CONTENT, note.getContent());


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        contentValues.put(COLUMN_NOTE_TIMESTAMP, timestamp);

        long result = db.update(TABLE_NOTES, contentValues,
                COLUMN_NOTE_ID + " = ?",
                new String[] { String.valueOf(note.getId()) });

        return result != -1;
    }

    public boolean deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NOTES,
                COLUMN_NOTE_ID + " = ?",
                new String[] { String.valueOf(id) });

        return result != -1;
    }

    public ArrayList<NotesModel> getAllNotes() {
        ArrayList<NotesModel> notesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES + " ORDER BY " +
                    COLUMN_NOTE_TIMESTAMP + " DESC", null);

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(COLUMN_NOTE_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_NOTE_TITLE);
                int contentIndex = cursor.getColumnIndex(COLUMN_NOTE_CONTENT);
                int timestampIndex = cursor.getColumnIndex(COLUMN_NOTE_TIMESTAMP);

                do {
                    int id = cursor.getInt(idIndex);
                    String title = cursor.getString(titleIndex);
                    String content = cursor.getString(contentIndex);
                    String timestamp = cursor.getString(timestampIndex);

                    NotesModel note = new NotesModel(id, title, content, timestamp);
                    notesList.add(note);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return notesList;
    }

    public NotesModel getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        NotesModel note = null;

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES + " WHERE " +
                    COLUMN_NOTE_ID + " = ?", new String[] { String.valueOf(id) });

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(COLUMN_NOTE_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_NOTE_TITLE);
                int contentIndex = cursor.getColumnIndex(COLUMN_NOTE_CONTENT);
                int timestampIndex = cursor.getColumnIndex(COLUMN_NOTE_TIMESTAMP);

                String title = cursor.getString(titleIndex);
                String content = cursor.getString(contentIndex);
                String timestamp = cursor.getString(timestampIndex);

                note = new NotesModel(id, title, content, timestamp);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return note;
    }
}