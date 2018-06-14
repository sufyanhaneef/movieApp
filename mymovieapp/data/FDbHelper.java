package sufyan.com.mymovieapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;

import java.util.ArrayList;
import java.util.List;

import sufyan.com.mymovieapp.model.Movie;

public class  FDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorite.db";

    private static final int DATABASE_VERSION = 1;

    public static final String LOGTAG = "FAVORITE";

    public FDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + favoriteContract.Entries.TABLE_NAME + " (" +
                favoriteContract.Entries._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                favoriteContract.Entries.COLUMN_MOVIEID + " INTEGER, " +
                favoriteContract.Entries.COLUMN_TITLE + " TEXT NOT NULL, " +
                favoriteContract.Entries.COLUMN_USERRATING + " REAL NOT NULL, " +
                favoriteContract.Entries.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                favoriteContract.Entries.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + favoriteContract.Entries.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

    public List<Movie> getAllFavorite(Context context){
        String[] columns = {
                favoriteContract.Entries._ID,
                favoriteContract.Entries.COLUMN_MOVIEID,
                favoriteContract.Entries.COLUMN_TITLE,
                favoriteContract.Entries.COLUMN_USERRATING,
                favoriteContract.Entries.COLUMN_POSTER_PATH,
                favoriteContract.Entries.COLUMN_PLOT_SYNOPSIS

        };
        String sortOrder =
                favoriteContract.Entries._ID + " ASC";
        List<Movie> favoriteList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(favoriteContract.Entries.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);

        if (cursor.moveToFirst()){
            do {
                Movie movie = new Movie();
                movie.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(favoriteContract.Entries.COLUMN_MOVIEID))));
                movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(favoriteContract.Entries.COLUMN_TITLE)));
                movie.setVoteAverage(Double.parseDouble(cursor.getString(cursor.getColumnIndex(favoriteContract.Entries.COLUMN_USERRATING))));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(favoriteContract.Entries.COLUMN_POSTER_PATH)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(favoriteContract.Entries.COLUMN_PLOT_SYNOPSIS)));

                favoriteList.add(movie);

            }while(cursor.moveToNext());
        }
        cursor.close();

        return favoriteList;
    }
}
