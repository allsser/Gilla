package com.ccj.gilla.TabFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.ccj.gilla.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TabFragment4 extends Fragment {

    EditText input01;
    TextView txtMsg;
    int select = 0;
    String lats[];
    String lngs[];
    public static String defaultUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=";

    Handler handler = new Handler();
    float Lat, Lng;

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
        View view = inflater.inflate(R.layout.tab_fragment_4, container, false);

        input01 = (EditText)view.findViewById(R.id.input01);
        txtMsg = (TextView)view.findViewById(R.id.txtMsg);

        Button button = (Button)view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String userStr = input01.getText().toString();
                String urlStr = defaultUrl + userStr + "&key=AIzaSyCqO9nJRkU-WH0d8gGfmtBEz3pdqbPA-eY&language=ko";

                ConnectThread thread = new ConnectThread(urlStr);
                thread.start();
            }
        });
        return view;
    }

    class ConnectThread extends Thread {
        String urlStr;

        public ConnectThread(String inStr) {
            urlStr = inStr;
        }

        public void run() {
            try {
                final String output = request(urlStr);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        findLatLng(output);
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

    private void findLatLng(String output) {
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
                    String addresses[] = new String[jsonResultLength];
                    lats = new String[jsonResultLength];
                    lngs = new String[jsonResultLength];

                    for(int i=0; i<jsonResultLength; i++) {
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
                            txtMsg.setText("lat : " + lats[select] + "\n lng : " + lngs[select]);
                        }
                    }).setNegativeButton("취소", null);
                    ab.show();
                } else if(jsonResultLength == 1){
                    JSONObject geoObject = new JSONObject(jsonResultArray.getJSONObject(0).getString("geometry"));
                    JSONObject locObject = new JSONObject(geoObject.getString("location"));
                    String lat = locObject.getString("lng");
                    String lng = locObject.getString("lat");

                    txtMsg.setText("lat : " + lat + "\n lng : " + lng);
                }
            } else {
                Toast.makeText(getActivity(), "해당 조회 결과 값이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
