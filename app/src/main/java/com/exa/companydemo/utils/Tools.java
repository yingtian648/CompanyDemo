package com.exa.companydemo.utils;

import android.content.Context;

import com.exa.companydemo.db.FilesDao;
import com.exa.companydemo.db.entity.Files;

import java.util.ArrayList;

public class Tools {

    public Runnable testDb(Context context) {
        return () -> {
            ArrayList<Files> files = new ArrayList<>();
            for (int i = 3000; i < 4000; i++) {
                files.add(makeFiles(i + 1));
            }
            FilesDao dao = new FilesDao(context);
            dao.insertFiles2(files);
        };
    }

    public static Files makeFiles(int index) {
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
