package com.exa.companyclient.provider;


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
    public String toString() {
        return "Files{" +
                "id=" + id +
                ", add_time=" + add_time +
                ", modify_time=" + modify_time +
                ", size=" + size +
                ", duration=" + duration +
                ", width=" + width +
                ", height=" + height +
                ", file_type=" + file_type +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", root_dir='" + root_dir + '\'' +
                ", mime_type='" + mime_type + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", display_name='" + display_name + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}
