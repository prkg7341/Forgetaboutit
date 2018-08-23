package com.jaewoo.forgetaboutit;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class PopupActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // 타이틀 바 제거
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 설정을 위한 준비
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // 팝업화면 출력시 배경이 검게 되지 않도록
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        // 팝업화면 출력시 투명도 설정
        layoutParams.dimAmount = 0.5f;

        //버튼 리스너 처리


    }
}
