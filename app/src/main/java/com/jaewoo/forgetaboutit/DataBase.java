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

    private static DataBase dataBase = null;
    private SQLiteDatabase db;

    public static DataBase openDB(Context context){
        if(dataBase==null){
            dataBase = new DataBase(context, "DB", 1);
        }
        return dataBase;
    }

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
                "갱신시간 Time, " +
                "지역 TEXT);");
        db.execSQL("CREATE TABLE SiSi " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "시도 TEXT, " +
                "시군구 TEXT);");
        db.execSQL("CREATE TABLE SiSiU " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "법정동코드 TEXT, " +
                "시도 TEXT," +
                "시군구 TEXT, " +
                "읍면동 Time);");
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

    public void insert(String table, String dataTime, String pm10Value, String pm10Grade1h, String pm25Value, String pm25Grade1h, String now, String region){

        db = getWritableDatabase();
        db.execSQL("INSERT INTO " + table + " VALUES(null, '" + dataTime + "', '" + pm10Value + "', '" + pm10Grade1h + "', '"
                //+ pm25Value + "', '" + pm25Grade1h + "', '" + now + "', '" + Main.userLocation + "');");
                + pm25Value + "', '" + pm25Grade1h + "', '" + now + "', '" + region + "');");
        db.close();
    }

    public void insert(String table, String sido, String sgg){
        db = getWritableDatabase();
        db.execSQL("INSERT INTO " + table + " VALUES(null, '" + sido + "', '" + sgg + "');");
        db.close();
    }

    public void insert(String table, String num, String sido, String sgg, String umd){
        db = getWritableDatabase();
        db.execSQL("INSERT INTO " + table + " VALUES(null, '" + num + "', '" + sido + "', '" + sgg + "', '" + umd + "');");
        db.close();
    }

    public String select(String table){
        db = getReadableDatabase();
        String st = null;
        try (Cursor c = db.rawQuery(
                "SELECT * " +
                        "FROM " + table + ";", null)) {
            Log.d(TAG, "입력된 데이터 개수: " + c.getCount());
            if (c.getCount() > 0) {
                c.moveToFirst();
                st = c.getString(1) + "-" + c.getString(2) + "-" + c.getString(3) + "-"
                        + c.getString(4) + "-" + c.getString(5) + "-" + c.getString(6) + "\n" + c.getString(7);
            }
        }
        db.close();
        Log.d(TAG, "select is " + st);
        return st;
    }

    public String select(String table, String sido){
        db = getReadableDatabase();
        String st;
        StringBuilder sb = new StringBuilder();
        try (Cursor c = db.rawQuery(
                "SELECT 시군구" +
                        " FROM " + table +
                        " WHERE 시도 = '" + sido + "';", null)) { // default 강원도
            Log.d(TAG, "입력된 데이터 개수: " + c.getCount());

            if (c.getCount() > 0) {
                c.moveToFirst();
               do {
                    sb.append(c.getString(0)).append("-");
                } while(c.moveToNext());
            }
            sb.deleteCharAt(sb.length()-1);
            st = sb.toString();
            db.close();
        }
        Log.d(TAG, "select is " + st);
        return st;
    }

    public String select(String table, String sido, String sgg){
        db = getReadableDatabase();
        String st;
        StringBuilder sb = new StringBuilder();
        try (Cursor c = db.rawQuery(
                "SELECT 읍면동" +
                        " FROM " + table +
                        " WHERE 시도 = '" + sido +
                        "' AND 시군구 = '" + sgg + "';", null)) { // default 강원도
            Log.d(TAG, "입력된 데이터 개수: " + c.getCount());

            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    sb.append(c.getString(0)).append("-");
                } while(c.moveToNext());
                sb.deleteCharAt(sb.length()-1);
            }

            st = sb.toString();
            db.close();
        }
        Log.d(TAG, "select is " + st);
        return st;
    }

    public int count(String table){

        // DB 읽기권한 가져옴
        db = getReadableDatabase();
        int num;
        try (Cursor c = db.rawQuery(
                "SELECT * " +
                        " FROM " + table + ";", null)) {
            if (c != null && c.getCount() > 0) {
                Log.d(TAG, "c.getCount() is " + c.getCount());
                c.moveToFirst();
                num = c.getCount();
            } else {
                num = 0;
            }
            db.close();
        }
        return num;
    }

    public void update(String table, String dataTime, String pm10Value, String pm10Grade1h, String pm25Value, String pm25Grade1h, String now, String region){
        db = getWritableDatabase();
        db.execSQL("UPDATE " + table
                + " SET 측정시간 = '" + dataTime + "', "
                + " 미세먼지농도 = '" + pm10Value + "', "
                + " 미세먼지등급 = '" + pm10Grade1h + "', "
                + " 초미세먼지농도 = '" + pm25Value + "', "
                + " 초미세먼지등급 = '" + pm25Grade1h + "', "
                + " 갱신시간 = '" + now + "', "
                //+ " 지역 = '" + Main.userLocation + "';");
                + " 지역 = '" + region + "';");
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
