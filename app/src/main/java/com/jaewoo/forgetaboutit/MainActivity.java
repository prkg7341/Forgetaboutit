package com.jaewoo.forgetaboutit;

import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*///사라진 버튼

        final RunningAsyncTask runAsync = new RunningAsyncTask();

        //Button button = (Button) findViewById(R.id.button);
        runAsync.execute();
        /*button.setOnClickListener(new View.OnClickListener() // the error is on this line (specifically the .setOnClickListener)
        {
            @Override
            public void onClick(View v)
            {
                runAsync.execute();
            }
        });*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }

    /*class RunningAsyncTask extends AsyncTask<String, String, String>{//동네예보
        String key = "kHyDlmh%2FCNeOpJZKLPsgHn0Hwo%2BkVzGLfSF2e8k6c3w0%2FbccHw7tu5TQ4UX8TRGBb8jwpEpT%2BKvi9%2FsWxfbRmA%3D%3D";
        StringBuilder urlBuilder;
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        StringBuilder sb = new StringBuilder();
        String line;
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());

        @Override
        protected String doInBackground(String... strings) {
            urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastSpaceData"); //URL
            try {
                urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + key); //서비스 인증
                urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); //오늘 발표
                urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode("0500", "UTF-8")); //05시 발표 * 기술문서 참조
                urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode("60", "UTF-8")); //예보지점의 X 좌표값
                urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode("127", "UTF-8")); //예보지점의 Y 좌표값
                urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("999", "UTF-8")); //한 페이지 결과 수
                //urlBuilder.append("&" + URLEncoder.encode("pageSize","UTF-8") + "=" + URLEncoder.encode("999", "UTF-8")); //페이지 사이즈
                urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //페이지 번호
                //urlBuilder.append("&" + URLEncoder.encode("startPage","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //시작 페이지
                urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8")); //xml(기본값), json
                url = new URL(urlBuilder.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                System.out.println("Response code: " + conn.getResponseCode());
                if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                while ((line = rd.readLine()) != null) {
                    sb.append(line + "\n");
                }
                rd.close();
                conn.disconnect();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }return sb.toString();
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView section_label = (TextView) findViewById(R.id.section_label);
            section_label.setText(s);
        }
    }*/

    /*class RunningAsyncTask extends AsyncTask<String, String, String>{ //수질정보
        String key;
        StringBuilder urlBuilder;
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        StringBuilder sb = new StringBuilder();
        String line;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            key = "kHyDlmh%2FCNeOpJZKLPsgHn0Hwo%2BkVzGLfSF2e8k6c3w0%2FbccHw7tu5TQ4UX8TRGBb8jwpEpT%2BKvi9%2FsWxfbRmA%3D%3D";
            urlBuilder = new StringBuilder("http://apis.data.go.kr/B500001/rwis/waterQuality/list"); //URL
            try {
                Date dt = new Date();
                urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + key); //Service Key
                urlBuilder.append("&" + URLEncoder.encode("stDt","UTF-8") + "=" + URLEncoder.encode(dt.toString(), "UTF-8")); //조회시작일자
                urlBuilder.append("&" + URLEncoder.encode("stTm","UTF-8") + "=" + URLEncoder.encode("08", "UTF-8")); //조회시작시간
                urlBuilder.append("&" + URLEncoder.encode("edDt","UTF-8") + "=" + URLEncoder.encode("2017-07-04", "UTF-8")); //조회종료일자
                urlBuilder.append("&" + URLEncoder.encode("edTm","UTF-8") + "=" + URLEncoder.encode("09", "UTF-8")); //조회종료시간
                urlBuilder.append("&" + URLEncoder.encode("fcltyMngNo","UTF-8") + "=" + URLEncoder.encode("4824012333", "UTF-8")); //시설관리번호
                urlBuilder.append("&" + URLEncoder.encode("sujCode","UTF-8") + "=" + URLEncoder.encode("333", "UTF-8")); //사업장코드
                urlBuilder.append("&" + URLEncoder.encode("liIndDiv","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //생활:1, 공업:2
                urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); //줄수
                urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //페이지번호
                url = new URL(urlBuilder.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                System.out.println("Response code: " + conn.getResponseCode());
                if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                while ((line = rd.readLine()) != null) {
                    sb.append(line + "\n");
                }
                rd.close();
                conn.disconnect();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }


        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView section_label = (TextView) findViewById(R.id.section_label);
            section_label.setText(s);
        }
    }*/
}
