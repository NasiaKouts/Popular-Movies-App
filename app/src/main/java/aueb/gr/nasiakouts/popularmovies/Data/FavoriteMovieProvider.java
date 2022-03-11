package aueb.gr.nasiakouts.popularmovies.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FavoriteMovieProvider extends ContentProvider{

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private FavoriteMovieDBHelper dbHelper;

    /*
        Codes for the UriMatcher
     */
    private static final int FAVORITE_MOVIE_TABLE = 100;
    private static final int FAVORITE_MOVIE_DETAILS = 101;

    @Override
    public boolean onCreate(){
        dbHelper = new FavoriteMovieDBHelper(getContext());
        return true;
    }

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // add a code for each type of URI
        matcher.addURI(FavoriteMovieContract.CONTENT_AUTHORITY,
                FavoriteMovieContract.PATH_TASKS, FAVORITE_MOVIE_TABLE);
        matcher.addURI(FavoriteMovieContract.CONTENT_AUTHORITY,
                FavoriteMovieContract.PATH_TASKS + "/*", FAVORITE_MOVIE_DETAILS);

        return matcher;
    }

    @Override
    public String getType(Uri uri){
        final int match = uriMatcher.match(uri);

        switch (match){
            case FAVORITE_MOVIE_TABLE:{
                return FavoriteMovieContract.FavoriteMovieEntry.CONTENT_DIR_TYPE;
            }
            case FAVORITE_MOVIE_DETAILS:{
                return FavoriteMovieContract.FavoriteMovieEntry.CONTENT_ITEM_TYPE;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }


    /*
        Called when adding a movie to favorites
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Get access to the database
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        // find the corresponding uri depending the match
        int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            // add favorite movie
            case FAVORITE_MOVIE_TABLE:
                // Insert new values into the database
                // Inserting values into tasks table
                long id = db.insert(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(FavoriteMovieContract.FavoriteMovieEntry.FAVORITE_MOVIE_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " +
                            FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri used for insertion: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        if(getContext() != null) getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    /*
        Called either on favorites fragment in order to display all favorite movies
        either on details activity when select on movie from the favorite fragment
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Get access to underlying database
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        // find the corresponding uri depending the match
        int match = uriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            // Query for the favorites directory (favorite fragment)
            case FAVORITE_MOVIE_TABLE:
                retCursor = db.query(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            // Query for a specific favorite movie's details (detail activity)
            case FAVORITE_MOVIE_DETAILS:
                retCursor =  db.query(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        FavoriteMovieContract.FavoriteMovieEntry.MOVIE_ID + "=?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        if(getContext() != null) retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }


    /*
        Called when removing a movie from the favorites
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int tasksDeleted;
        String id;

        // Get access to the database
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        // find the corresponding uri depending the match
        int match = uriMatcher.match(uri);

        switch (match) {
            /* for future use, in case adding delete all favorites */
            case FAVORITE_MOVIE_TABLE:
                tasksDeleted = db.delete(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME, "1", null);
                break;

            // delete corresponding movie from the favorites
            case FAVORITE_MOVIE_DETAILS:
                // Get the task ID from the URI path
                id = uri.getPathSegments().get(1);
                tasksDeleted = db.delete(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        FavoriteMovieContract.FavoriteMovieEntry.MOVIE_ID + "=?", new String[]{id});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (tasksDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }

    /* ignore this for now, we don't need to update the favorites db */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
