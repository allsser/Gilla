package com.ccj.gilla.TabFragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.ccj.gilla.R;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class TabFragment4 extends Fragment {

    private WebView daum_webView;
    private TextView daum_result;
    private Handler handler;


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

        daum_result = (TextView) view.findViewById(R.id.daum_result);
        // WebView 설정
        daum_webView = (WebView) view.findViewById(R.id.daum_webview);

        // WebView 초기화
        init_webView();
        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();
        return view;
    }

    public void init_webView() {

        // JavaScript 허용
        daum_webView.getSettings().setJavaScriptEnabled(true);
        // JavaScript의 window.open 허용
        daum_webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        daum_webView.addJavascriptInterface(new AndroidBridge(), "TestApp");
        // web client 를 chrome 으로 설정
        daum_webView.setWebChromeClient(new WebChromeClient());
        // webview url load. php 파일 주소
        daum_webView.loadUrl("http://192.168.25.60:80/daum_address.php");
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    daum_result.setText(String.format("(%s) %s %s", arg1, arg2, arg3));
                    // WebView를 초기화 하지않으면 재사용할 수 없음
                    init_webView();
                }
            });
        }
    }
}
