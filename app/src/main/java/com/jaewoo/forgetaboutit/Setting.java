package com.jaewoo.forgetaboutit;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;

public class Setting extends Fragment {

    public Setting(){

    }

    DataBase dataBase;
    static String st1;
    static String st2;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.setting, container, false);

        dataBase = DataBase.openDB(getActivity());

        // values의 strings에 저장된 array: sido를 가져옴
        Resources res = getResources();
        String[] sido = res.getStringArray(R.array.sido);

        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        final Spinner spinner2 = (Spinner) view.findViewById(R.id.spinner2);
        final Spinner spinner3 = (Spinner) view.findViewById(R.id.spinner3);

        String st3;

        if(dataBase.count("SiSi")<250 || dataBase.count("SiSiU")<5048) {
            copyExcelDataToDatabase();
        }

        final ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, sido);
        spinner.setAdapter(arrayAdapter);

        st1 = spinner.getSelectedItem().toString();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                st1 = spinner.getSelectedItem().toString();
                ArrayAdapter arrayAdapter2 = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, dataBase.select("SiSi", st1).split("-"));
                spinner2.setAdapter(arrayAdapter2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                st2 = spinner2.getSelectedItem().toString();
                ArrayAdapter arrayAdapter3 = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, dataBase.select("SiSiU", st1, st2).split("-"));
                spinner3.setAdapter(arrayAdapter3);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //ArrayAdapter arrayAdapter3 = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, );
        //spinner3.setAdapter(arrayAdapter3);



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
}
