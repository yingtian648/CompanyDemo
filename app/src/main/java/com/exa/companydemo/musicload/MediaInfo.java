package com.exa.companydemo.musicload;

public class MediaInfo {
    public String path;
    public String title;
    public String artList;
    public String album;
    public String albumArtist;
    public int duration;
    public long size;
    public long displayName;
    public int width;
    public int height;
    public String composer;//创作者

    public MediaInfo() {
    }

    public MediaInfo(String path, String title, String artList, String album, String albumArtist, int duration, long size, long displayName, String composer) {
        this.path = path;
        this.title = title;
        this.artList = artList;
        this.album = album;
        this.albumArtist = albumArtist;
        this.duration = duration;
        this.size = size;
        this.displayName = displayName;
        this.composer = composer;
    }

    @Override
    public String toString() {
        return "MediaInfo{" +
                "path='" + path + '\'' +
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
