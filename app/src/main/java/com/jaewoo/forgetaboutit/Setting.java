package com.jaewoo.forgetaboutit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Objects;

import jxl.Sheet;
import jxl.Workbook;

public class Setting extends Fragment {

    public Setting(){

    }

    DataBase dataBase;
    public static String st;
    public static String st1;
    public static String st2;
    public static String st3;
    public static String starthour, startmin, endhour, endmin;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.setting, container, false);

        dataBase = DataBase.openDB(getActivity());

        // values의 strings에 저장된 array: sido를 가져옴
        Resources res = getResources();
        String[] sido = res.getStringArray(R.array.sido);
        String[] hour = res.getStringArray(R.array.hour);
        String[] min = res.getStringArray(R.array.min);

        final Spinner sidoSpinner = (Spinner) view.findViewById(R.id.sidoSpinner);
        final Spinner sggSpinner = (Spinner) view.findViewById(R.id.sggSpinner);
        final Spinner umdSpinner = (Spinner) view.findViewById(R.id.umdSpinner);
        final Spinner starthourSpinner = (Spinner) view.findViewById(R.id.starthourSpinner);
        final Spinner startminSpinner = (Spinner) view.findViewById(R.id.startminSpinner);
        final Spinner endhourSpinner = (Spinner) view.findViewById(R.id.endhourSpinner);
        final Spinner endminSpinner = (Spinner) view.findViewById(R.id.endminSpinner);

        final ArrayAdapter sidoArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, sido);//my_list_item_1
        final ArrayAdapter hourArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, hour);
        final ArrayAdapter minArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, min);

        sidoSpinner.setAdapter(sidoArrayAdapter);

        starthourSpinner.setAdapter(hourArrayAdapter);
        startminSpinner.setAdapter(minArrayAdapter);
        endhourSpinner.setAdapter(hourArrayAdapter);
        endminSpinner.setAdapter(minArrayAdapter);

        st1 = sidoSpinner.getSelectedItem().toString();

        sidoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                st1 = sidoSpinner.getSelectedItem().toString();
                if(st1.split("")[2].compareTo("남")==0 || st1.split("")[2].compareTo("북")==0){
                    st = st1.substring(0,2);
                }
                else{
                    st = st1.substring(0,1);
                }
                ArrayAdapter arrayAdapter2 = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, dataBase.select("SiSi", st1).split("-"));
                sggSpinner.setAdapter(arrayAdapter2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sggSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                st2 = sggSpinner.getSelectedItem().toString();
                ArrayAdapter arrayAdapter3 = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, dataBase.select("SiSiU", st1, st2).split("-"));
                umdSpinner.setAdapter(arrayAdapter3);
                st3 = umdSpinner.getSelectedItem().toString();
                if(st2.compareTo("")==0){
                    st2 = "세종시";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        umdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                st3 = umdSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        starthour = starthourSpinner.getSelectedItem().toString();
        startmin = startminSpinner.getSelectedItem().toString();
        endhour = endhourSpinner.getSelectedItem().toString();
        endmin = endminSpinner.getSelectedItem().toString();

        starthourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                starthour = starthourSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        startminSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startmin = startminSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        endhourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endhour = endhourSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        endminSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endmin = endminSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final CheckBox air = (CheckBox) view.findViewById(R.id.checkBox);

        if(air.isChecked()){
            starthourSpinner.getSelectedView().setEnabled(true);
            startminSpinner.getSelectedView().setEnabled(true);
            endhourSpinner.getSelectedView().setEnabled(true);
            endminSpinner.getSelectedView().setEnabled(true);
        }
        else{
            starthourSpinner.setEnabled(false);
            startminSpinner.setEnabled(false);
            endhourSpinner.setEnabled(false);
            endminSpinner.setEnabled(false);
        }

        air.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(air.isChecked()){
                    starthourSpinner.setEnabled(true);
                    startminSpinner.setEnabled(true);
                    endhourSpinner.setEnabled(true);
                    endminSpinner.setEnabled(true);
                }
                else{
                    starthourSpinner.setEnabled(false);
                    startminSpinner.setEnabled(false);
                    endhourSpinner.setEnabled(false);
                    endminSpinner.setEnabled(false);
                }
            }
        }) ;

        if(dataBase.count("SiSi")<250 || dataBase.count("SiSiU")<5048) {
            copyExcelDataToDatabase();
        }

        // 매 시각 5분마다 파싱,
        //repeatedAlarm();

        return view;
    }

    private void copyExcelDataToDatabase() {

        Workbook workbook1 = null;
        Workbook workbook2 = null;
        Sheet sheet = null;

        try {
            InputStream is1 = getResources().getAssets().open("2.xls");
            InputStream is2 = getResources().getAssets().open("4.xls");
            workbook1 = Workbook.getWorkbook(is1);
            workbook2 = Workbook.getWorkbook(is2);

            if(dataBase.count("SiSi")<250) {
                sheet = workbook1.getSheet(0);
            }

            if (sheet != null) {

                int nMaxColumn = 2;
                int nRowStartIndex = 0;
                int nRowEndIndex = sheet.getColumn(nMaxColumn - 1).length - 1;
                int nColumnStartIndex = 0;
                //int nColumnEndIndex = sheet.getRow(2).length - 1;

                for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                    String sido = sheet.getCell(nColumnStartIndex, nRow).getContents();
                    String sgg = sheet.getCell(nColumnStartIndex + 1, nRow).getContents();
                    dataBase.insert("SiSi",sido,sgg);
                }
                dataBase.close();
            }

            if(dataBase.count("SiSiU")<5048) {
                sheet = workbook2.getSheet(0);
            }

            if (sheet != null) {

                int nMaxColumn = 2;
                int nRowStartIndex = 0;
                int nRowEndIndex = sheet.getColumn(nMaxColumn - 1).length - 1;
                int nColumnStartIndex = 0;
                //int nColumnEndIndex = sheet.getRow(2).length - 1;

                for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                    String num = sheet.getCell(nColumnStartIndex, nRow).getContents();
                    String sido = sheet.getCell(nColumnStartIndex + 1, nRow).getContents();
                    String sgg = sheet.getCell(nColumnStartIndex + 2, nRow).getContents();
                    String umd = sheet.getCell(nColumnStartIndex + 3, nRow).getContents();
                    dataBase.insert("SiSiU", num, sido, sgg, umd);
                }
                dataBase.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook1 != null && workbook2!=null) {
                workbook1.close();
            }
        }
    }

    public void repeatedAlarm() {

        AlarmManager am = (AlarmManager) Objects.requireNonNull(getActivity()).getSystemService(getActivity().ALARM_SERVICE);

        // 실행될 intent
        Intent intent = new Intent(getActivity(), PushPopup.class);
        PendingIntent sender = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar parsingTime = Calendar.getInstance();

        //알람시간 calendar에 set
        parsingTime.set(parsingTime.get(Calendar.YEAR), parsingTime.get(Calendar.MONTH), parsingTime.get(Calendar.DATE),
                parsingTime.get(Calendar.HOUR), parsingTime.get(Calendar.MINUTE), parsingTime.get(Calendar.SECOND));

        // 매 시각 5분마다 반복하도록 알람 예약
        am.setRepeating(AlarmManager.RTC_WAKEUP, parsingTime.getTimeInMillis(), 5*1000L, sender);
    }
}
