package com.exa.companyclient.utils;

import com.exa.baselib.utils.L;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class TestLock {
    private final BlockingDeque<String> mInsertValueEntryQue =
            new LinkedBlockingDeque<>();
    private int MAX_TEST_TIMES = 500;

    private ExecutorService service = Executors.newFixedThreadPool(2);

    public void startTest(){
        for (int i = 0; i < MAX_TEST_TIMES; i++) {
            final int index = i;
            service.execute(()->{
                addTask(index);
            });
            service.execute(()->{
                pullTask(index);
            });
        }
    }

    private void addTask(int index){
        synchronized (mInsertValueEntryQue){
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mInsertValueEntryQue.addLast(String.valueOf(System.currentTimeMillis()));
        }
        L.d("addTask end");
        if(index == MAX_TEST_TIMES-1){
            L.d("addTask FINISH");
        }
    }

    private void pullTask(int index){
        if (mInsertValueEntryQue.size() >= 20) {
            synchronized (mInsertValueEntryQue) {
                if (mInsertValueEntryQue.size() >= 20) {
                    List<String> insertValueEntryLists = new ArrayList<>();
                    String entry;
                    while (!mInsertValueEntryQue.isEmpty()) {
                        try {
                            entry = mInsertValueEntryQue.take();
                            insertValueEntryLists.add(entry);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        L.d("pullTask end");
        if(index == MAX_TEST_TIMES-1){
            L.d("pullTask FINISH");
        }
    }
}
