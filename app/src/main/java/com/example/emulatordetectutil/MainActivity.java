package com.example.emulatordetectutil;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        boolean isEmulator = EmulatorDetectUtil.getSingleInstance().isEmulator(this);
        tv.setText("是否是模拟器：" + isEmulator);
        Log.d("x7测试", "是否是模拟器：" + isEmulator);
    }
}
