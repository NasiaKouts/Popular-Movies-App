package aueb.gr.nasiakouts.popularmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FavoriteMovieDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = FavoriteMovieDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "FavoriteMovie.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteMovieDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " +
                FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME + " (" +
                FavoriteMovieContract.FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteMovieContract.FavoriteMovieEntry.MOVIE_ID + " TEXT, " +
                FavoriteMovieContract.FavoriteMovieEntry.TITLE + " TEXT, " +
                FavoriteMovieContract.FavoriteMovieEntry.BACKDROP_IMAGE + " TEXT, " +
                FavoriteMovieContract.FavoriteMovieEntry.GENRES_ID + " TEXT, " +
                FavoriteMovieContract.FavoriteMovieEntry.GENRES_NAME + " TEXT, " +
                FavoriteMovieContract.FavoriteMovieEntry.ORIGINAL_LANGUAGE + " TEXT, " +
                FavoriteMovieContract.FavoriteMovieEntry.SPOKEN_LANGUAGES + " TEXT, " +
                FavoriteMovieContract.FavoriteMovieEntry.PLOT_SUMMARY + " TEXT, " +
                FavoriteMovieContract.FavoriteMovieEntry.POPULARITY + " TEXT, " +
                FavoriteMovieContract.FavoriteMovieEntry.POSTER_IMAGE + " TEXT, " +
                FavoriteMovieContract.FavoriteMovieEntry.RELEASE_DATE + " TEXT, " +
                FavoriteMovieContract.FavoriteMovieEntry.COLLECTION + " TEXT, " +
                FavoriteMovieContract.FavoriteMovieEntry.LENGTH + " TEXT, " +
                FavoriteMovieContract.FavoriteMovieEntry.RATING + " TEXT, " +
                FavoriteMovieContract.FavoriteMovieEntry.TRAILER_KEY + " TEXT, " +
                FavoriteMovieContract.FavoriteMovieEntry.TRAILER_NAME + " TEXT" +
        ");";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");

        final String SQL_DROP_FAVORITE_MOVIE_TABLE = "DROP TABLE IF EXISTS " +
                FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME;

        sqLiteDatabase.execSQL(SQL_DROP_FAVORITE_MOVIE_TABLE);

        onCreate(sqLiteDatabase);
    }
}
