package com.exa.companydemo.mediacenterthreadpool;

import com.exa.baselib.utils.L;

public class TestUtil {
    public static void  test(){
        ParsePoolUtil mParsePoolUtil = ParsePoolUtil.getInstance();
        mParsePoolUtil.start();
        mParsePoolUtil.setOnCompletedCallback(new ParsePoolUtil.ScanCompleteCallback() {
            @Override
            public void onCompleted() {
                L.e("完成扫描");
            }
        });
        for (int i = 0; i < 5 ; i++) {
            Task task = new Task();
            int finalI = i;
            task.setCallback(task1 -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                L.d("scanFile:" + finalI);
            });
            mParsePoolUtil.addTask(task);
        }
        Task task = new Task();
        task.setCallback(new TaskCallback() {
            @Override
            public void onProcess(Task task) {
                mParsePoolUtil.shutdown("");
            }
        });
        mParsePoolUtil.addTask(task);
        task = new Task();
        task.setCallback(new TaskCallback() {
            @Override
            public void onProcess(Task task) {
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                L.d("scanFile:" + 111);
            }
        });
        mParsePoolUtil.addTask(task);
    }
}
