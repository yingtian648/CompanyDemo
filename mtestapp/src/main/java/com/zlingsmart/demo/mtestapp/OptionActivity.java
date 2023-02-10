package com.zlingsmart.demo.mtestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * @author Administrator
 */
public class OptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        initView();
        initData();
    }

    private void initView() {

    }

    private void initData() {
        setTitle(getIntent().getStringExtra(Constants.ExtraField.TITLE));
    }
}