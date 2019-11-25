package com.ccj.gilla.TabFragment;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ccj.gilla.R;

import java.util.ArrayList;

public class TabFragment1 extends Fragment {

    TextView textView;

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

        final EditText origin = (EditText) view.findViewById(R.id.origin);
        final EditText destination = (EditText) view.findViewById(R.id.destination);
        final EditText mode = (EditText) view.findViewById(R.id.mode);
        final EditText departure_time = (EditText) view.findViewById(R.id.departure_time);

        Button searchBtn = (Button) view.findViewById(R.id.button);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼을 눌렀을 때 서비스를 생성하고 실행.
                Intent i = new Intent();
                // 명시적 Intent를 사용.
                ComponentName cname = new ComponentName("com.ccj.gilla",
                        "com.ccj.gilla.Service.MapService");
                i.setComponent(cname);
                // 사용자가 입력한 문자열(toString()사용해서 문자열로 바꿈) 받아옴
                i.putExtra("origin", origin.getText().toString());
                i.putExtra("destination", destination.getText().toString());
                i.putExtra("mode", mode.getText().toString());
                i.putExtra("departure_time", departure_time.getText().toString());
                getActivity().startService(i);
            }
        });




        Intent i = new Intent();
        String  result = i.getExtras().getString("resultData");
        Log.i("KAKAOLog", "데이터가 정상적으로 Activity에 도달!!");
        TextView test = (TextView)view.findViewById(R.id.text);
        test.setText(result);

        return view;
    }


}
