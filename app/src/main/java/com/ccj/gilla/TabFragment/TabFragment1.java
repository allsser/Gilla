package com.ccj.gilla.TabFragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ccj.gilla.Alarm_Reciver;
import com.ccj.gilla.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ALARM_SERVICE;


public class TabFragment1 extends Fragment implements OnMapReadyCallback {



    private GoogleMap mMap;
    EditText origin, destination;
    int select = 0;
    String addresses[];
    String lats[];
    String lngs[];
    String originLocation, destinatLocation;
    public static String defaultUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    String Item = "0";

    AlarmManager alarm_manager;
    TimePicker alarm_timepicker;
    Context context;
    PendingIntent pendingIntent;

    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // onCreate 부분은 Fragment가 생성될때 호출되는 부분이다.
        super.onCreate(savedInstanceState);



    }


    @Nullable
    @Override
    // LayoutInflater:안드로이드에서 View를 만드는 가장 기본적인 방법
    // ViewGroup:안드로이드에서 뷰에 배치
    // Bundle savedInstanceState:세로모드에서 가로모드로 변할때 전역변수에 설정한 값들을 유지시켜 준다.
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // onCreateView는 onCreate 후에 화면을 구성할때 호출되는 부분이다.
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        origin = (EditText)view.findViewById(R.id.origin);
        destination = (EditText)view.findViewById(R.id.destination);

        Button originBtn = (Button)view.findViewById(R.id.originBtn);
        originBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String userStr = origin.getText().toString();
                String originUrlStr = defaultUrl + userStr + "&key=AIzaSyCqO9nJRkU-WH0d8gGfmtBEz3pdqbPA-eY&language=ko";

                TabFragment1.originConnectThread thread = new TabFragment1.originConnectThread(originUrlStr);
                thread.start();
            }
        });

        Button destinationBtn = (Button)view.findViewById(R.id.destinationBtn);
        destinationBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String userStr = destination.getText().toString();
                String destinationUrlStr = defaultUrl + userStr + "&key=AIzaSyCqO9nJRkU-WH0d8gGfmtBEz3pdqbPA-eY&language=ko";

                TabFragment1.detinatConnectThread thread = new TabFragment1.detinatConnectThread(destinationUrlStr);
                thread.start();
            }
        });

        this.context = getActivity();

        // 알람매니저 설정
        alarm_manager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

        // 타임피커 설정
        alarm_timepicker = view.findViewById(R.id.time_picker1);

        // Calendar 객체 생성
        final Calendar calendar = Calendar.getInstance();

        // 알람리시버 intent 생성
        final Intent my_intent = new Intent(this.context, Alarm_Reciver.class);

        final EditText origin = (EditText) view.findViewById(R.id.origin);
        final EditText destination = (EditText) view.findViewById(R.id.destination);

        Button startBtn = (Button) view.findViewById(R.id.start);
        Button stopBtn = (Button) view.findViewById(R.id.stop);


        startBtn.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                // 버튼을 눌렀을 때 서비스를 생성하고 실행.
                Intent i = new Intent();
                // 명시적 Intent를 사용.
                ComponentName cname = new ComponentName("com.ccj.gilla",
                        "com.ccj.gilla.Service.MapService");
                i.setComponent(cname);
                // 사용자가 입력한 문자열(toString()사용해서 문자열로 바꿈) 받아옴
                i.putExtra("origin", originLocation);
                i.putExtra("destination", destinatLocation);
                getActivity().startService(i);




                // calendar에 시간 셋팅
//                calendar.set(Calendar.HOUR_OF_DAY, alarm_timepicker.getHour());
                calendar.set(Calendar.HOUR_OF_DAY, 05);
                calendar.set(Calendar.MINUTE, 32);



                // 시간 가져옴
                int hour = alarm_timepicker.getHour();
                int minute = alarm_timepicker.getMinute();


                Toast.makeText(getActivity(),"Alarm 예정 " + hour + "시 " + minute + "분",Toast.LENGTH_SHORT).show();

                // reveiver에 string 값 넘겨주기
                my_intent.putExtra("extra","alarm on");

                pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, my_intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                // 알람셋팅
                alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        pendingIntent);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Alarm 종료",Toast.LENGTH_SHORT).show();
                // 알람매니저 취소
                alarm_manager.cancel(pendingIntent);

                my_intent.putExtra("extra","alarm off");

                // 알람취소
                getContext().sendBroadcast(my_intent);
            }
        });



        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return view;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    class originConnectThread extends Thread {
        String urlStr;

        public originConnectThread(String inStr) {
            urlStr = inStr;
        }

        public void run() {
            try {
                final String output = request(urlStr);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        originfindLatLng(output);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String request(String urlStr) {
            StringBuilder output = new StringBuilder();
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Accept-Charset", "URF-8");

                    int resCode = conn.getResponseCode();

                    Log.i("resCode", String.valueOf(resCode));
                    if (resCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                        String line = null;
                        while (true) {
                            line = reader.readLine();
                            if (line == null) {
                                break;
                            }
                            output.append(line + "\n");
                        }

                        reader.close();
                        conn.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return output.toString();
        }
    }

    private void originfindLatLng(String output) {
        Log.i("output", output);
        try {
            JSONObject jsonObject = new JSONObject(output);
            String status = jsonObject.getString("status");
            String condition = status.trim();

            if(condition.equals("OK")) {
                JSONArray jsonResultArray = new JSONArray(jsonObject.getString("results"));
                int jsonResultLength = jsonResultArray.length();

                if(jsonResultLength > 5) {
                    Toast.makeText(getActivity(), "검색된 결과 값이 너무 많습니다.", Toast.LENGTH_SHORT).show();
                } else if(jsonResultLength > 1) {
                    addresses = new String[jsonResultLength];
                    lats = new String[jsonResultLength];
                    lngs = new String[jsonResultLength];

                    for(int i=0; i<jsonResultLength; i++) {
                        String address = jsonResultArray.getJSONObject(i).getString( "formatted_address");

                        JSONObject geoObject = new JSONObject(jsonResultArray.getJSONObject(i).getString("geometry"));
                        JSONObject locObject = new JSONObject(geoObject.getString("location"));
                        String lat = locObject.getString("lat");
                        String lng = locObject.getString("lng");

                        addresses[i] = address;
                        lats[i] = lat;
                        lngs[i] = lng;
                    }

                    AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                    ab.setTitle("아래에서 해당 주소를 선택하세요");
                    ab.setSingleChoiceItems(addresses, select, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            select = i;
                        }
                    }).setPositiveButton("선택", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            originLocation = lats[select] + "," + lngs[select];
                            Log.i("originlocation",originLocation);
                            origin.setText(addresses[select]);
                        }
                    }).setNegativeButton("취소", null);
                    ab.show();
                } else if(jsonResultLength == 1){
                    String address = jsonResultArray.getJSONObject(0).getString( "formatted_address");

                    JSONObject geoObject = new JSONObject(jsonResultArray.getJSONObject(0).getString("geometry"));
                    JSONObject locObject = new JSONObject(geoObject.getString("location"));
                    String lat = locObject.getString("lat");
                    String lng = locObject.getString("lng");

                    originLocation = lat + "," + lng;
                    Log.i("originlocation",originLocation);
                    origin.setText(address);
                }
            } else {
                Toast.makeText(getActivity(), "해당 조회 결과 값이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class detinatConnectThread extends Thread {
        String urlStr;

        public detinatConnectThread(String inStr) {
            urlStr = inStr;
        }

        public void run() {
            try {
                final String output = request(urlStr);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        detinatfindLatLng(output);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String request(String urlStr) {
            StringBuilder output = new StringBuilder();
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Accept-Charset", "URF-8");

                    int resCode = conn.getResponseCode();

                    Log.i("resCode", String.valueOf(resCode));
                    if (resCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                        String line = null;
                        while (true) {
                            line = reader.readLine();
                            if (line == null) {
                                break;
                            }
                            output.append(line + "\n");
                        }

                        reader.close();
                        conn.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return output.toString();
        }
    }

    private void detinatfindLatLng(String output) {
        Log.i("output", output);
        try {
            JSONObject jsonObject = new JSONObject(output);
            String status = jsonObject.getString("status");
            String condition = status.trim();

            if (condition.equals("OK")) {
                JSONArray jsonResultArray = new JSONArray(jsonObject.getString("results"));
                int jsonResultLength = jsonResultArray.length();

                if (jsonResultLength > 5) {
                    Toast.makeText(getActivity(), "검색된 결과 값이 너무 많습니다.", Toast.LENGTH_SHORT).show();
                } else if (jsonResultLength > 1) {
                    addresses = new String[jsonResultLength];
                    lats = new String[jsonResultLength];
                    lngs = new String[jsonResultLength];

                    for (int i = 0; i < jsonResultLength; i++) {
                        String address = jsonResultArray.getJSONObject(i).getString("formatted_address");

                        JSONObject geoObject = new JSONObject(jsonResultArray.getJSONObject(i).getString("geometry"));
                        JSONObject locObject = new JSONObject(geoObject.getString("location"));
                        String lat = locObject.getString("lat");
                        String lng = locObject.getString("lng");

                        addresses[i] = address;
                        lats[i] = lat;
                        lngs[i] = lng;
                    }

                    AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                    ab.setTitle("아래에서 해당 주소를 선택하세요");
                    ab.setSingleChoiceItems(addresses, select, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            select = i;
                        }
                    }).setPositiveButton("선택", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            destinatLocation = lats[select] + "," + lngs[select];
                            Log.i("destinatLocation", destinatLocation);
                            destination.setText(addresses[select]);
                        }
                    }).setNegativeButton("취소", null);
                    ab.show();
                } else if (jsonResultLength == 1) {
                    String address = jsonResultArray.getJSONObject(0).getString("formatted_address");

                    JSONObject geoObject = new JSONObject(jsonResultArray.getJSONObject(0).getString("geometry"));
                    JSONObject locObject = new JSONObject(geoObject.getString("location"));
                    String lat = locObject.getString("lat");
                    String lng = locObject.getString("lng");

                    destinatLocation = lat + "," + lng;
                    Log.i("destinatLocation", destinatLocation);
                    destination.setText(address);
                }
            } else {
                Toast.makeText(getActivity(), "해당 조회 결과 값이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
