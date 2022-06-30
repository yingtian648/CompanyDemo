package com.exa.companydemo.db;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MMediaProvider extends ContentProvider {
    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (matcher.match(uri)){
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

    private static class  MediaCodes{
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
        matcher.addURI("media", "*/images/media", MediaCodes.IMAGES_MEDIA);
        matcher.addURI("media", "*/images/media/#", MediaCodes.IMAGES_MEDIA_ID);
        matcher.addURI("media", "*/images/thumbnails", MediaCodes.IMAGES_THUMBNAILS);
        matcher.addURI("media", "*/images/thumbnails/#", MediaCodes.IMAGES_THUMBNAILS_ID);

        matcher.addURI("media", "*/audio/media",MediaCodes. AUDIO_MEDIA);
        matcher.addURI("media", "*/audio/media/#", MediaCodes.AUDIO_MEDIA_ID);
        matcher.addURI("media", "*/audio/media/#/genres", MediaCodes.AUDIO_MEDIA_ID_GENRES);
        matcher.addURI("media", "*/audio/media/#/genres/#", MediaCodes.AUDIO_MEDIA_ID_GENRES_ID);
        matcher.addURI("media", "*/audio/media/#/playlists", MediaCodes.AUDIO_MEDIA_ID_PLAYLISTS);
        matcher.addURI("media", "*/audio/media/#/playlists/#", MediaCodes.AUDIO_MEDIA_ID_PLAYLISTS_ID);
        matcher.addURI("media", "*/audio/genres", MediaCodes.AUDIO_GENRES);
        matcher.addURI("media", "*/audio/genres/#", MediaCodes.AUDIO_GENRES_ID);
        matcher.addURI("media", "*/audio/genres/#/members", MediaCodes.AUDIO_GENRES_ID_MEMBERS);
        matcher.addURI("media", "*/audio/genres/all/members", MediaCodes.AUDIO_GENRES_ALL_MEMBERS);
        matcher.addURI("media", "*/audio/playlists", MediaCodes.AUDIO_PLAYLISTS);
        matcher.addURI("media", "*/audio/playlists/#", MediaCodes.AUDIO_PLAYLISTS_ID);
        matcher.addURI("media", "*/audio/playlists/#/members", MediaCodes.AUDIO_PLAYLISTS_ID_MEMBERS);
        matcher.addURI("media", "*/audio/playlists/#/members/#", MediaCodes.AUDIO_PLAYLISTS_ID_MEMBERS_ID);
        matcher.addURI("media", "*/audio/artists", MediaCodes.AUDIO_ARTISTS);
        matcher.addURI("media", "*/audio/artists/#", MediaCodes.AUDIO_ARTISTS_ID);
        matcher.addURI("media", "*/audio/artists/#/albums", MediaCodes.AUDIO_ARTISTS_ID_ALBUMS);
        matcher.addURI("media", "*/audio/albums", MediaCodes.AUDIO_ALBUMS);
        matcher.addURI("media", "*/audio/albums/#", MediaCodes.AUDIO_ALBUMS_ID);
        matcher.addURI("media", "*/audio/albumart", MediaCodes.AUDIO_ALBUMART);
        matcher.addURI("media", "*/audio/albumart/#", MediaCodes.AUDIO_ALBUMART_ID);
        matcher.addURI("media", "*/audio/media/#/albumart", MediaCodes.AUDIO_ALBUMART_FILE_ID);

        matcher.addURI("media", "*/video/media", MediaCodes.VIDEO_MEDIA);
        matcher.addURI("media", "*/video/media/#", MediaCodes.VIDEO_MEDIA_ID);
        matcher.addURI("media", "*/video/thumbnails", MediaCodes.VIDEO_THUMBNAILS);
        matcher.addURI("media", "*/video/thumbnails/#", MediaCodes.VIDEO_THUMBNAILS_ID);

        matcher.addURI("media", "*/media_scanner", MediaCodes.MEDIA_SCANNER);

        matcher.addURI("media", "*/fs_id", MediaCodes.FS_ID);
        matcher.addURI("media", "*/version", MediaCodes.VERSION);

        matcher.addURI("media", "*/mtp_connected", MediaCodes.MTP_CONNECTED);

        matcher.addURI("media", "*", MediaCodes.VOLUMES_ID);
        matcher.addURI("media", null, MediaCodes.VOLUMES);

        // Used by MTP implementation
        matcher.addURI("media", "*/file", MediaCodes.FILES);
        matcher.addURI("media", "*/file/#", MediaCodes.FILES_ID);
        matcher.addURI("media", "*/object", MediaCodes.MTP_OBJECTS);
        matcher.addURI("media", "*/object/#", MediaCodes.MTP_OBJECTS_ID);
        matcher.addURI("media", "*/object/#/references", MediaCodes.MTP_OBJECT_REFERENCES);

        // Used only to trigger special logic for directories
        matcher.addURI("media", "*/dir", MediaCodes.FILES_DIRECTORY);

        /**
         * @deprecated use the 'basic' or 'fancy' search Uris instead
         */
        matcher.addURI("media", "*/audio/" + SearchManager.SUGGEST_URI_PATH_QUERY,
                MediaCodes.AUDIO_SEARCH_LEGACY);
        matcher.addURI("media", "*/audio/" + SearchManager.SUGGEST_URI_PATH_QUERY + "/*",
                MediaCodes.AUDIO_SEARCH_LEGACY);

        // used for search suggestions
        matcher.addURI("media", "*/audio/search/" + SearchManager.SUGGEST_URI_PATH_QUERY,
                MediaCodes.AUDIO_SEARCH_BASIC);
        matcher.addURI("media", "*/audio/search/" + SearchManager.SUGGEST_URI_PATH_QUERY +
                "/*", MediaCodes.AUDIO_SEARCH_BASIC);

        // used by the music app's search activity
        matcher.addURI("media", "*/audio/search/fancy", MediaCodes.AUDIO_SEARCH_FANCY);
        matcher.addURI("media", "*/audio/search/fancy/*", MediaCodes.AUDIO_SEARCH_FANCY);
    }
}
