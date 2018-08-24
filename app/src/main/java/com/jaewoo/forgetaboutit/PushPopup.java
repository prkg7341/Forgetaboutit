package com.jaewoo.forgetaboutit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Constraints;
import android.support.design.widget.TabItem;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PushPopup extends BroadcastReceiver{

    public PushPopup(){

    }

    View view;
    Context context;

    DataBase dataBase = DataBase.openDB(context);

    @Override
    public void onReceive(Context context, Intent intent) {

        // 매 시각 파싱 기능 (미구현)
        /*Air air = new Air();
        air.getAirInfo(view);*/

        if(dataBase.select("Air").split("-")[3].contains("나쁨")){
            Log.d("미세먼지 수치가","나쁨이상입니다");
            // 팝업기능 삽입 필요
        }
    }
}
