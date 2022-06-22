package com.exa.companydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.exa.companydemo.db.FilesDao;
import com.exa.companydemo.db.entity.Files;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ExecutorService pool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();
    }

    private void test() {
        pool = Executors.newScheduledThreadPool(2);
        FilesDao dao = new FilesDao(this);
        pool.execute(() -> {
            ArrayList<Files> files = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                files.add(makeFiles(i + 1));
            }
            dao.insertFiles(files);
        });
    }

    private Files makeFiles(int index) {
        Files files = new Files();
        long time = System.currentTimeMillis();
        files.name = "不要说话";
        files.add_time = index;
        files.modify_time = index;
        files.size = 2155421;
        files.duration = 315000;
        files.width = 0;
        files.height = 0;
        files.file_type = 1;
        files.path = "/sd/dsds/" + index;
        files.root_dir = "/sd/dsds/" + index;
        files.mime_type = "video/mp4";
        files.artist = "陈奕迅";
        files.album = "SHDJSUNHDJSHJDS";
        files.display_name = String.valueOf(index);
        files.tags = null;
        return files;
    }
}