package com.jaewoo.forgetaboutit;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class DataBase extends SQLiteOpenHelper {

    DataBase(Context context, String name, int version) {
        super(context, name, null, version);
    }

    private SQLiteDatabase db;

    // 최초에 데이터베이스가 없을경우, 데이터베이스 생성을 위해 호출됨
    // 테이블 생성하는 코드를 작성한다
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "DB 생성");
        db.execSQL("CREATE TABLE Air " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "측정시간 TEXT, " +
                "미세먼지농도 TEXT," +
                "미세먼지등급 TEXT, " +
                "초미세먼지농도 TEXT, " +
                "초미세먼지등급 TEXT, " +
                "갱신시간 Time);");
        Log.d(TAG, "Table 생성");
        // 필요한 테이블들 create
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // 데이터베이스의 버전이 바뀌었을 때 호출되는 콜백 메서드
        // 버전이 바뀌었을 때 기존데이터베이스를 어떻게 변경할 것인지 작성한다
        // 각 버전의 변경 내용들을 버전마다 작성해야함

        String sql_drop_all = "drop table if exists Air;"; // 테이블 드랍
        db.execSQL(sql_drop_all);

        onCreate(db);
    }

    public void insert(String table, String dataTime, int pm10Value, String pm10Grade1h, int pm25Value, String pm25Grade1h, int now){

        db = getWritableDatabase();
        db.execSQL("INSERT INTO " + table + " VALUES(null, '" + dataTime + "', '" + pm10Value + "', '" + pm10Grade1h + "', '"
                + pm25Value + "', '" + pm25Grade1h + "', '" + now + "');");
        Log.d(TAG, "Data was inserted");
        db.close();
    }

    public String select(String table){
        db = getReadableDatabase();
        String st = null;
        try (Cursor c = db.rawQuery(
                "SELECT * " +
                        " FROM " + table + ";", null)) {
            Log.d(TAG, "cursor is " + (c.getCount() == 0));
            if (c.getCount() > 0) {
                Log.d(TAG, "이거 실행 되나요?");
                c.moveToFirst();
                st = c.getString(1) + " " + c.getString(2) + " " + c.getString(3) + " "
                        + c.getString(4) + " " + c.getString(5) + " " + c.getString(6);
            }
        }
        db.close();
        Log.d(TAG, "select is " + st);
        return st;
    }

    public int count(String table){

        // DB 읽기권한 가져옴
        db = getReadableDatabase();
        int num;
        try (Cursor c = db.rawQuery(
                "SELECT count (ID) " +
                        " FROM " + table + ";", null)) {
            if (c != null && c.getCount() > 0) {
                Log.d(TAG, "c.getCount() is " + c.getCount());
                c.moveToFirst();
                num = c.getCount();
            } else {
                num = 0;
            }
            db.close();
            Log.d(TAG, "select count is " + c.getCount());
        }
        return num;
    }

    public void update(String table, String dataTime, int pm10Value, String pm10Grade1h, int pm25Value, String pm25Grade1h, int now){
        db = getWritableDatabase();
        db.execSQL("UPDATE " + table
                + " SET 측정시간 = '" + dataTime + "', "
                + " 미세먼지농도 = '" + pm10Value + "', "
                + " 미세먼지등급 = '" + pm10Grade1h + "', "
                + " 초미세먼지농도 = '" + pm25Value + "', "
                + " 초미세먼지등급 = '" + pm25Grade1h + "', "
                + " 갱신시간 = '" + now + "';");
        db.close();
        Log.d(TAG, "DB was updated");
    }

    public void delete(String table) {
        db = getWritableDatabase();
        db.execSQL("DELETE FROM " + table + ";");
        db.close();
        Log.d(TAG, "Data was deleted");
    }
}
