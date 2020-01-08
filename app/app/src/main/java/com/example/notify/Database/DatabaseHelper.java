package com.example.sjecnotify.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="User.db";
    public static final String TABLE_NAME="user_favourites";
    public static final String col1="noticeTitle";


    public DatabaseHelper( Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
            String query="create table user_favourites(noticeTitle text)";
            db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user_favourites");
        onCreate(db);
    }

    public void addToFavourites(String noticeid)
    {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("INSERT INTO user_favourites(noticeTitle) VALUES ('%s'); ",noticeid);
        db.execSQL(query);
    }

    public boolean insertData(String noticeTitle)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(col1,noticeTitle);
        long result=db.insert(TABLE_NAME,null,contentValues);
        if(result==-1)
                return false;
        else
            return true;

    }

    public boolean deleteData(String noticetitle)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(col1,noticetitle);
        long result=db.delete(TABLE_NAME,  col1 + "=?", new String[]{noticetitle});
        if(result==-1)
            return false;
        else
            return true;
    }

    public void removeFromFavourites(String noticeid)
    {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("DELETE FROM user_favourites WHERE noticeTitle='%s'; ",noticeid);
        db.execSQL(query);
    }

    public boolean isFavourites(String noticeid)
    {
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("SELECT * FROM user_favourites WHERE noticeTitle='%s'; ",noticeid);
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.getCount()<=0)
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
