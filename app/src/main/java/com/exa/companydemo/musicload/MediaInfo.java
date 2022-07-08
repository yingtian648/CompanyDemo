package com.exa.companydemo.musicload;

public class MediaInfo {
    public String path;
    public String author;
    public String title;//MediaMetadataRetriever.METADATA_KEY_TITLE
    public String artList;//MediaMetadataRetriever.METADATA_KEY_ARTIST
    public String album;//专辑名
    public String albumArtist;
    public String mimeType;//媒体类型
    public String suffix;//后缀名（小写）mp3 jpeg mp4 rm wma等
    public int duration;
    public long size;
    public String displayName;
    public int width;//图片宽度
    public int height;//图片高度
    public double lat;//纬度
    public double lon;//经度
    public String composer;//创作者

    @Override
    public String toString() {
        return "MediaInfo{" +
                "title='" + title + '\'' +
                ", displayName=" + displayName +
                ", path='" + path + '\'' +
                ", author='" + author + '\'' +
                ", artList='" + artList + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", suffix='" + suffix + '\'' +
                ", album='" + album + '\'' +
                ", albumArtist='" + albumArtist + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", width=" + width +
                ", height=" + height +
                ", lat=" + lat +
                ", lon=" + lon +
                ", composer='" + composer + '\'' +
                '}' + "\n";
    }
}
