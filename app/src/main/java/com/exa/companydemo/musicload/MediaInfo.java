package com.exa.companydemo.musicload;

public class MediaInfo {
    public String name;
    public String path;
    public String title;
    public String artList;
    public String album;//专辑
    public String albumArtist;
    public int duration;
    public long size;
    public String displayName;
    public int width;
    public int height;
    public String composer;//创作者

    @Override
    public String toString() {
        return "MediaInfo{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", title='" + title + '\'' +
                ", artList='" + artList + '\'' +
                ", album='" + album + '\'' +
                ", albumArtist='" + albumArtist + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", displayName=" + displayName +
                ", width=" + width +
                ", height=" + height +
                ", composer='" + composer + '\'' +
                '}' + "\n";
    }
}
