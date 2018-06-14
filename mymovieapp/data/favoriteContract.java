package sufyan.com.mymovieapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class favoriteContract {
    public static final String CONTENT_AUTHORITY="sufyan.com.mymovieapp";
    public static final Uri BASE_CONTENT_URL= Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_MOVIE="favorite";

    public static final class Entries implements BaseColumns{

        public static final Uri CONTENT_URI=BASE_CONTENT_URL.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_MOVIE;

        public static final String TABLE_NAME="favorite";
        public static final String COLUMN_MOVIEID="movieId";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_USERRATING="userRating";
        public static final String COLUMN_POSTER_PATH="posterpath";
        public static final String COLUMN_PLOT_SYNOPSIS="overView";
    }
}
