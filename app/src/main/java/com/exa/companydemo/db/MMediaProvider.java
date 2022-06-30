package com.exa.companydemo.db;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.exa.companydemo.base.App;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MMediaProvider extends ContentProvider {
    private final String TAG = MMediaProvider.class.getSimpleName();
    private static final String AUTHORITY = "media";
    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private FilesDao dao;

    @Override
    public boolean onCreate() {
        dao = new FilesDao(App.getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query: " + uri + "\u3000\u3000selection:" + selection + "\u3000\u3000sortOrder:" + sortOrder
                + "\u3000\u3000selectionArgs:" + (selectionArgs == null ? "null" : selectionArgs.toString()));
//        int code = matcher.match(uri);//这里可以根据code来查询对应的表格
        SQLiteDatabase db = dao.getHelper().getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String limit = uri.getQueryParameter("limit");
        queryBuilder.setTables("files");
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder, limit);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (matcher.match(uri)) {
            case MediaCodes.IMAGES_MEDIA:
                return MediaStore.Images.Media.CONTENT_TYPE;
            case MediaCodes.AUDIO_MEDIA:
                return MediaStore.Audio.Media.CONTENT_TYPE;
            case MediaCodes.VIDEO_MEDIA:
                return MediaStore.Video.Media.CONTENT_TYPE;
        }
        throw new IllegalStateException("Unknown URI : " + uri);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    private static class MediaCodes {
        private static final int IMAGES_MEDIA = 1;
        private static final int IMAGES_MEDIA_ID = 2;
        private static final int IMAGES_THUMBNAILS = 3;
        private static final int IMAGES_THUMBNAILS_ID = 4;
        private static final int AUDIO_MEDIA = 100;
        private static final int AUDIO_MEDIA_ID = 101;
        private static final int AUDIO_MEDIA_ID_GENRES = 102;
        private static final int AUDIO_MEDIA_ID_GENRES_ID = 103;
        private static final int AUDIO_MEDIA_ID_PLAYLISTS = 104;
        private static final int AUDIO_MEDIA_ID_PLAYLISTS_ID = 105;
        private static final int AUDIO_GENRES = 106;
        private static final int AUDIO_GENRES_ID = 107;
        private static final int AUDIO_GENRES_ID_MEMBERS = 108;
        private static final int AUDIO_GENRES_ALL_MEMBERS = 109;
        private static final int AUDIO_PLAYLISTS = 110;
        private static final int AUDIO_PLAYLISTS_ID = 111;
        private static final int AUDIO_PLAYLISTS_ID_MEMBERS = 112;
        private static final int AUDIO_PLAYLISTS_ID_MEMBERS_ID = 113;
        private static final int AUDIO_ARTISTS = 114;
        private static final int AUDIO_ARTISTS_ID = 115;
        private static final int AUDIO_ALBUMS = 116;
        private static final int AUDIO_ALBUMS_ID = 117;
        private static final int AUDIO_ARTISTS_ID_ALBUMS = 118;
        private static final int AUDIO_ALBUMART = 119;
        private static final int AUDIO_ALBUMART_ID = 120;
        private static final int AUDIO_ALBUMART_FILE_ID = 121;
        private static final int VIDEO_MEDIA = 200;
        private static final int VIDEO_MEDIA_ID = 201;
        private static final int VIDEO_THUMBNAILS = 202;
        private static final int VIDEO_THUMBNAILS_ID = 203;
        private static final int VOLUMES = 300;
        private static final int VOLUMES_ID = 301;
        private static final int AUDIO_SEARCH_LEGACY = 400;
        private static final int AUDIO_SEARCH_BASIC = 401;
        private static final int AUDIO_SEARCH_FANCY = 402;
        private static final int MEDIA_SCANNER = 500;
        private static final int FS_ID = 600;
        private static final int VERSION = 601;
        private static final int FILES = 700;
        private static final int FILES_ID = 701;
        // Used only by the MTP implementation
        private static final int MTP_OBJECTS = 702;
        private static final int MTP_OBJECTS_ID = 703;
        private static final int MTP_OBJECT_REFERENCES = 704;
        // UsbReceiver calls insert() and delete() with this URI to tell us
        // when MTP is connected and disconnected
        private static final int MTP_CONNECTED = 705;
        // Used only to invoke special logic for directories
        private static final int FILES_DIRECTORY = 706;
        // Used only by the storage table
        private static final int STORAGE = 800;
        private static final int STORAGE_ID = 801;
    }

    static {
        matcher.addURI(AUTHORITY, "*/images/media", MediaCodes.IMAGES_MEDIA);
        matcher.addURI(AUTHORITY, "*/images/media/#", MediaCodes.IMAGES_MEDIA_ID);
        matcher.addURI(AUTHORITY, "*/images/thumbnails", MediaCodes.IMAGES_THUMBNAILS);
        matcher.addURI(AUTHORITY, "*/images/thumbnails/#", MediaCodes.IMAGES_THUMBNAILS_ID);

        matcher.addURI(AUTHORITY, "*/audio/media", MediaCodes.AUDIO_MEDIA);
        matcher.addURI(AUTHORITY, "*/audio/media/#", MediaCodes.AUDIO_MEDIA_ID);
        matcher.addURI(AUTHORITY, "*/audio/media/#/genres", MediaCodes.AUDIO_MEDIA_ID_GENRES);
        matcher.addURI(AUTHORITY, "*/audio/media/#/genres/#", MediaCodes.AUDIO_MEDIA_ID_GENRES_ID);
        matcher.addURI(AUTHORITY, "*/audio/media/#/playlists", MediaCodes.AUDIO_MEDIA_ID_PLAYLISTS);
        matcher.addURI(AUTHORITY, "*/audio/media/#/playlists/#", MediaCodes.AUDIO_MEDIA_ID_PLAYLISTS_ID);
        matcher.addURI(AUTHORITY, "*/audio/genres", MediaCodes.AUDIO_GENRES);
        matcher.addURI(AUTHORITY, "*/audio/genres/#", MediaCodes.AUDIO_GENRES_ID);
        matcher.addURI(AUTHORITY, "*/audio/genres/#/members", MediaCodes.AUDIO_GENRES_ID_MEMBERS);
        matcher.addURI(AUTHORITY, "*/audio/genres/all/members", MediaCodes.AUDIO_GENRES_ALL_MEMBERS);
        matcher.addURI(AUTHORITY, "*/audio/playlists", MediaCodes.AUDIO_PLAYLISTS);
        matcher.addURI(AUTHORITY, "*/audio/playlists/#", MediaCodes.AUDIO_PLAYLISTS_ID);
        matcher.addURI(AUTHORITY, "*/audio/playlists/#/members", MediaCodes.AUDIO_PLAYLISTS_ID_MEMBERS);
        matcher.addURI(AUTHORITY, "*/audio/playlists/#/members/#", MediaCodes.AUDIO_PLAYLISTS_ID_MEMBERS_ID);
        matcher.addURI(AUTHORITY, "*/audio/artists", MediaCodes.AUDIO_ARTISTS);
        matcher.addURI(AUTHORITY, "*/audio/artists/#", MediaCodes.AUDIO_ARTISTS_ID);
        matcher.addURI(AUTHORITY, "*/audio/artists/#/albums", MediaCodes.AUDIO_ARTISTS_ID_ALBUMS);
        matcher.addURI(AUTHORITY, "*/audio/albums", MediaCodes.AUDIO_ALBUMS);
        matcher.addURI(AUTHORITY, "*/audio/albums/#", MediaCodes.AUDIO_ALBUMS_ID);
        matcher.addURI(AUTHORITY, "*/audio/albumart", MediaCodes.AUDIO_ALBUMART);
        matcher.addURI(AUTHORITY, "*/audio/albumart/#", MediaCodes.AUDIO_ALBUMART_ID);
        matcher.addURI(AUTHORITY, "*/audio/media/#/albumart", MediaCodes.AUDIO_ALBUMART_FILE_ID);

        matcher.addURI(AUTHORITY, "*/video/media", MediaCodes.VIDEO_MEDIA);
        matcher.addURI(AUTHORITY, "*/video/media/#", MediaCodes.VIDEO_MEDIA_ID);
        matcher.addURI(AUTHORITY, "*/video/thumbnails", MediaCodes.VIDEO_THUMBNAILS);
        matcher.addURI(AUTHORITY, "*/video/thumbnails/#", MediaCodes.VIDEO_THUMBNAILS_ID);

        matcher.addURI(AUTHORITY, "*/media_scanner", MediaCodes.MEDIA_SCANNER);

        matcher.addURI(AUTHORITY, "*/fs_id", MediaCodes.FS_ID);
        matcher.addURI(AUTHORITY, "*/version", MediaCodes.VERSION);

        matcher.addURI(AUTHORITY, "*/mtp_connected", MediaCodes.MTP_CONNECTED);

        matcher.addURI(AUTHORITY, "*", MediaCodes.VOLUMES_ID);
        matcher.addURI(AUTHORITY, null, MediaCodes.VOLUMES);

        // Used by MTP implementation
        matcher.addURI(AUTHORITY, "*/file", MediaCodes.FILES);
        matcher.addURI(AUTHORITY, "*/file/#", MediaCodes.FILES_ID);
        matcher.addURI(AUTHORITY, "*/object", MediaCodes.MTP_OBJECTS);
        matcher.addURI(AUTHORITY, "*/object/#", MediaCodes.MTP_OBJECTS_ID);
        matcher.addURI(AUTHORITY, "*/object/#/references", MediaCodes.MTP_OBJECT_REFERENCES);

        // Used only to trigger special logic for directories
        matcher.addURI(AUTHORITY, "*/dir", MediaCodes.FILES_DIRECTORY);

        /**
         * @deprecated use the 'basic' or 'fancy' search Uris instead
         */
        matcher.addURI(AUTHORITY, "*/audio/" + SearchManager.SUGGEST_URI_PATH_QUERY,
                MediaCodes.AUDIO_SEARCH_LEGACY);
        matcher.addURI(AUTHORITY, "*/audio/" + SearchManager.SUGGEST_URI_PATH_QUERY + "/*",
                MediaCodes.AUDIO_SEARCH_LEGACY);

        // used for search suggestions
        matcher.addURI(AUTHORITY, "*/audio/search/" + SearchManager.SUGGEST_URI_PATH_QUERY,
                MediaCodes.AUDIO_SEARCH_BASIC);
        matcher.addURI(AUTHORITY, "*/audio/search/" + SearchManager.SUGGEST_URI_PATH_QUERY +
                "/*", MediaCodes.AUDIO_SEARCH_BASIC);

        // used by the music app's search activity
        matcher.addURI(AUTHORITY, "*/audio/search/fancy", MediaCodes.AUDIO_SEARCH_FANCY);
        matcher.addURI(AUTHORITY, "*/audio/search/fancy/*", MediaCodes.AUDIO_SEARCH_FANCY);
    }
}
