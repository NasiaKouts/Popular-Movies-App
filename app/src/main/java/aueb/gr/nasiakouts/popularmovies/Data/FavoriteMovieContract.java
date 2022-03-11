package aueb.gr.nasiakouts.popularmovies.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteMovieContract {
    public static final String CONTENT_AUTHORITY = "aueb.gr.nasiakouts.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TASKS = "favorite_movie";

    public static final class FavoriteMovieEntry implements BaseColumns {

        public static final Uri FAVORITE_MOVIE_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public static Uri buildFlavorsUri(long movieId){
            return ContentUris.withAppendedId(FAVORITE_MOVIE_URI, movieId);
        }

        public static final String TABLE_NAME = "favorite_movie";

        public static final String MOVIE_ID = "movie_id";
        public static final String TITLE = "title";
        public static final String BACKDROP_IMAGE = "landscape_image";
        public static final String GENRES_ID = "genres_id";
        public static final String GENRES_NAME = "genres_name";
        public static final String ORIGINAL_LANGUAGE = "language";
        public static final String SPOKEN_LANGUAGES = "spoken_languages";
        public static final String PLOT_SUMMARY = "plot";
        public static final String POPULARITY = "popularity";
        public static final String POSTER_IMAGE = "poster";
        public static final String RELEASE_DATE = "release";
        public static final String COLLECTION = "collection";
        public static final String LENGTH = "length";
        public static final String RATING = "vote_average";
        public static final String TRAILER_KEY = "trailer_key";
        public static final String TRAILER_NAME = "trailer_name";


        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
    }
}
