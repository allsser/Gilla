package com.ccj.gilla.Service;

import android.app.Service;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.IBinder;
import android.util.Log;

import com.ccj.gilla.TabFragment.TabFragment1;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import androidx.annotation.Nullable;

public class MapService extends Service {
    private class MapRunnable implements Runnable {
        private String origin;
        private String destination;
        private String mode;
        private String departure_time;
        private String key = "AIzaSyCd94bckaxJaYfyhKVRFn8nDqSFlgb01AY";

        MapRunnable(String origin, String destination, String mode, String departure_time) {
            this.origin = origin;
            this.destination = destination;
            this.mode = mode;
            this.departure_time = departure_time;
        }

        @Override
        public void run() {
            String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + origin +
                    "&destination=" + destination +
                    "&mode=" + mode +
                    "&departure_time=" + departure_time +
                    "&key=" + key;

            try {
                URL urlOb = new URL(url);
                HttpURLConnection con = (HttpURLConnection)urlOb.openConnection();
                con.setRequestMethod("GET");

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream())
                );
                String line = null;
                String sb = "";
                while((line = br.readLine()) != null) {
                    sb+=line;
                }
                br.close();
                Log.i("sb", sb);

                JSONObject jsonObject = new JSONObject(sb);
                JSONArray jArr = jsonObject.getJSONArray("routes");

                JSONObject jsonObject1 = (JSONObject)jArr.get(0);
                Log.i("test1", jsonObject1.toString());

                JSONArray  jArr1 = jsonObject1.getJSONArray("legs");
                Log.i("test2", jArr1.toString());

                JSONObject jsonObject2 = (JSONObject)jArr1.get(0);
                Log.i("test3", jsonObject2.toString());

                JSONObject jsonObject3 = (JSONObject)jsonObject2.get("duration");
                Log.i("test4", jsonObject3.toString());

                String mins = (String)jsonObject3.get("text");
                Log.i("test5", mins);

                Intent i = new Intent(getApplicationContext(), TabFragment1.class);

                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                i.putExtra("resultData",mins);
                startActivity(i);

            } catch (Exception e) {
                Log.i("Error", e.toString());
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        // 서비스 객체가 만들어지는 시점에 1회 호출
        // 사용할 resource를 준비하는 과정.
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // onCreat()후에 자동적을 호출되며
        // startService()에 의해서 호출된다.
        // 실제 로직처리는 onStartCommand에서 진행
        Log.i("KAKAOBOOKLog","onStartCommand 호출!!");
        // 전달된 키워드를 이용해서 외부 네트워크 접속을 위한
        // Thread를 하나 생성해야 한다.
        String origin = intent.getExtras().getString("origin");
        String destination = intent.getExtras().getString("destination");
        String mode = intent.getExtras().getString("mode");
        String departure_time = intent.getExtras().getString("departure_time");
        // Thread를 만들기 위한 Runnable 객체부터 생성
        MapRunnable runnable = new MapRunnable(origin, destination, mode, departure_time);
        Thread t = new Thread(runnable);
        t.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // 서비스 객체가 메모리상에서 삭제될 때 1번 호출
        // 사용한 resource를 정리하는 과정.
        super.onDestroy();
    }
}
