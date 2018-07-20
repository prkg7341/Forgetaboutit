package com.jaewoo.forgetaboutit;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DataBase extends SQLiteOpenHelper {
    public DataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Main main = new Main();

        db.execSQL("CREATE TABLE test_table " +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "test TEXT );");

        Toast.makeText(main.getActivity(), "Toast Test", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
