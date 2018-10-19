package com.test.android.applicationa.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LinkContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.test.android.applicationa.database";

    public static final Uri URI_LINK = Uri.parse(
            "content://" + AUTHORITY + "/" + "link");

    public static final int TASKS = 100;
    public static final int TASK_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY, "link", TASKS);
        uriMatcher.addURI(AUTHORITY, "link" + "/*", TASK_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final int code = sUriMatcher.match(uri);
        if (code == TASKS || code == TASK_WITH_ID) {
            final Context context = getContext();
            if (context == null) {
                return null;
            }
            LinkDao linkDao = AppDatabase.getInstance(context).linkDao();
            List<LinkData> linkDataList = new ArrayList<>();
            final Cursor cursor;
            if (code == TASKS) {
                linkDataList = linkDao.loadLinks();
                cursor = getCursorFromList(linkDataList);
            } else {
                linkDataList = linkDao.selectById(ContentUris.parseId(uri));
                cursor = getCursorFromList(linkDataList);
            }
            cursor.setNotificationUri(context.getContentResolver(), uri);
            return cursor;
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case TASKS:
                final Context context = getContext();
                if (context == null) {
                    return null;
                }
                long insertedId = AppDatabase.getInstance(context).linkDao()
                        .insertLink(fromContentValues(values));
                context.getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, insertedId);
            case TASK_WITH_ID:
                throw new IllegalArgumentException("Invalid URI, cannot insert with ID: " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case TASKS:
                throw new IllegalArgumentException("Invalid URI, cannot update without ID" + uri);
            case TASK_WITH_ID:
                final Context context = getContext();
                if (context == null) {
                    return 0;
                }
                final int count = AppDatabase.getInstance(context).linkDao()
                        .deleteById(ContentUris.parseId(uri));
                context.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case TASKS:
                throw new IllegalArgumentException("Invalid URI, cannot update without ID" + uri);
            case TASK_WITH_ID:
                final Context context = getContext();
                if (context == null) {
                    return 0;
                }
                final LinkData linkData = fromContentValues(values);
                long longId = ContentUris.parseId(uri);
                linkData.setId(Integer.valueOf(Long.toString(longId)));
                final int count = AppDatabase.getInstance(context).linkDao()
                        .updateLink(linkData);
                context.getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case TASKS:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + "link";
            case TASK_WITH_ID:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + "link";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    public Cursor getCursorFromList(List<LinkData> linkList) {
        MatrixCursor matrixCursor = new MatrixCursor(new String[] {"id", "link", "status", "open_time"});

        for ( LinkData link : linkList ) {
            matrixCursor.newRow()
                    .add("id", link.getId())
                    .add("link", link.getLink())
                    .add("status", link.getStatus())
                    .add("open_time", link.getOpenTime());
        }
        return matrixCursor;
    }

    public static LinkData fromContentValues(ContentValues values) {
        final LinkData linkData = new LinkData();
        if (values.containsKey("link")) {
            linkData.setLink(values.getAsString("link"));
        }
        if (values.containsKey("status")) {
            linkData.setStatus(values.getAsInteger("status"));
        }
        if (values.containsKey("open_time")) {
            linkData.setOpenTime(DateConverter.toDate(values.getAsLong("open_time")));
        }
        return linkData;
    }
}
