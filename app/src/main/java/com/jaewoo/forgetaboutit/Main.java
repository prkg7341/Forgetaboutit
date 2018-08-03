package com.jaewoo.forgetaboutit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Main extends Fragment {

    public Main() {

    }

    // fragment가 return될 때 실행되는 메소드
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // 초기 화면을 "main"으로 설정
        final View view = inflater.inflate(R.layout.main, container, false);
        // 새로고침 버튼 생성
        Button renew = (Button) view.findViewById(R.id.renew);
        // 버튼 클릭시 내용을 출력할 TextView 생성 - 사용자의 위치를 출력하도록 수정예정
        final TextView userLocation = (TextView) view.findViewById(R.id.userLocation);

        // 버튼 클릭 리스너 생성 - 새로고침 기능으로 수정예정
        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼 클릭시 userLocation TextView에 "The renew works well!" 메시지 출력 - 수정예정
                userLocation.setText("The renew works well!");
            }
        });
        return view;
    }
}