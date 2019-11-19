package com.ccj.gilla;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TabFragment1 extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    // LayoutInflater:안드로이드에서 View를 만드는 가장 기본적인 방법
    // ViewGroup:안드로이드에서 뷰에 배치
    // Bundle savedInstanceState:세로모드에서 가로모드로 변할때 전역변수에 설정한 값들을 유지시켜 준다.
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_1, container, false);
    }
}
