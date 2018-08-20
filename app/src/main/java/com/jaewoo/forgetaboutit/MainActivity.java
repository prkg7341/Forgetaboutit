package com.jaewoo.forgetaboutit;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // 앱 실행시 실행되는 메소드
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 초기 화면을 "activity_main"으로 설정
        setContentView(R.layout.activity_main);

        // 각 페이지별로 fragment를 return할 어댑터 생성
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // 어댑터에서 return된 fragment를 보여줄 ViewPager 생성
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // 페이지 전환에 필요한 TabLayout 생성
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // 페이지 전환에 필요한 리스너 생성
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        final DataBase database = new DataBase(this, "FAI", 1);
    }

    // 각 페이지별로 fragment를 return할 어댑터의 클래스
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // 각 페이지별로 return할 fragment 생성
        Main main = new Main();
        Setting setting = new Setting();

        // 페이지 번호(position)별로 return할 fragment 설정
        @Override
        public Fragment getItem(int position) {
            // 페이지 번호(position)이 0이면 main을 return
            if(position==0) return main;
            // 페이지 번호(position)이 1이면 setting을 return
            else return setting;
        }

        // 총 페이지 수 설정
        @Override
        public int getCount() {
            return 2;
        }
    }
}
