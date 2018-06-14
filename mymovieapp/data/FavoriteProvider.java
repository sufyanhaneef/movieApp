package sufyan.com.mymovieapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static sufyan.com.mymovieapp.data.favoriteContract.CONTENT_AUTHORITY;
import static sufyan.com.mymovieapp.data.favoriteContract.PATH_MOVIE;

public class FavoriteProvider extends ContentProvider {

    public static final String LOG_TAG = FavoriteProvider.class.getSimpleName();
    public static final int MOVIE_ID = 101;
    private static final int MOVIE = 100;
    private static final String FIRST_ROW = "1";

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_MOVIE, MOVIE);
    }

    private FDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new FDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        int match = uriMatcher.match(uri);
        switch (match) {
            case MOVIE: {
                cursor = database.query(favoriteContract.Entries.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
        }
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                return favoriteContract.Entries.CONTENT_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MOVIE:
                long id = db.insert(favoriteContract.Entries.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(favoriteContract.Entries.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to Insert in Row");
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknow Uri" + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = uriMatcher.match(uri);

        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        if (null == selection) {
            selection = "1";
        }
        int movieDelete;
        switch (match) {
            case MOVIE:
                movieDelete = database.delete(favoriteContract.Entries.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return movieDelete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = uriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                return update(uri, values, selection, selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
