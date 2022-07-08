package com.exa.companyclient.provider;


import java.util.Objects;

/*
            "create table "
            + "files"
            + "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "name TEXT,path TEXT,root_dir TEXT,add_time INTEGER,modify_time INTEGER,mime_type TEXT,"
            + "size INTEGER,duration INTEGER,width INTEGER,height INTEGER,artist TEXT,"
            + "album TEXT,display_name TEXT,file_type INTEGER,tags TEXT)";
 */
public class Files {
    public long id;
    public long add_time;
    public long modify_time;
    public long size;
    public Integer duration;
    public Integer width;
    public Integer height;
    public Integer file_type;
    public String name;
    public String path;
    public String root_dir;
    public String mime_type;
    public String artist;
    public String album;
    public String display_name;
    public String tags;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Files)) return false;
        Files files = (Files) o;
        return id == files.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Files{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", size=" + size +
                ", mime_type='" + mime_type + '\'' +
                ", duration=" + duration +
                ", add_time=" + add_time +
                ", modify_time=" + modify_time +
                ", display_name='" + display_name + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", file_type=" + file_type +
                ", root_dir='" + root_dir + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
