package com.exa.companydemo;

import android.os.Bundle;
import android.view.View;

import com.exa.companydemo.db.entity.Files;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private ExecutorService pool;
    private ArrayList<Files> files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();
    }

    private void test() {

    }

    public void clickBtn(View view) {

    }
}