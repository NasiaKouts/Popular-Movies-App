package aueb.gr.nasiakouts.popularmovies.Utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/*
    This class is pretty common. It is responsible to build the Urls needed.
    Also, it is responsible to get the http response.
 */
public class NetworkUtils {

    /*
        TMDB API
     */
    private final static String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private final static String KEY_PARAM = "api_key";
    private final static String VIDEOS = "videos";
    private final static String REVIEWS = "reviews";
    private final static String KEY_VALUE = "2eb84bd8273cede35450dc30ead21d6a";

    /*
        Build the Url getting the popular or top rated movies,
        depending on the input param.
     */
    public static URL buildSortUrl(String sortBy) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(sortBy)
                .appendQueryParameter(KEY_PARAM, KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e("Malformed URL exception", e.getMessage());
        }
        return url;
    }

    /*
        Build the Url getting the details of the movie with the given id.
     */
    public static URL buildDetailsUrl(String movieId) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendQueryParameter(KEY_PARAM, KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e("Malformed URL exception", e.getMessage());
        }
        return url;
    }

    /*
        Build the Url getting the trailers of the movie with the given id.
     */
    public static URL buildGetVideosUrl(String movieId){
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(VIDEOS)
                .appendQueryParameter(KEY_PARAM, KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e("Malformed URL exception", e.getMessage());
        }
        return url;
    }

    /*
        Build the Url getting the reviews of the movie with the given id.
    */
    public static URL buildGetReviewsUrl(String movieId){
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(REVIEWS)
                .appendQueryParameter(KEY_PARAM, KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e("Malformed URL exception", e.getMessage());
        }
        return url;
    }


    /*
    YOUTUBE
    e.g.    https://www.youtube.com/watch?v=SUXWAEX2jlg - play video
            https://img.youtube.com/vi/SUXWAEX2jlg/mqdefault.jpg - get thumbnail
    */
    private final static String HTTPS = "https://";
    private final static String VIDEO_REQUEST_PRE = "www.";
    private final static String THUMBNAIL_REQUEST_PRE = "img.";
    private final static String YT_BASE_URL = "youtube.com";
    private final static String YT_VIDEO_PATH = "watch";
    private final static String YT_THUMBNAIL_PATH = "vi";
    private final static String YT_VIDEO_KEY_PARAM = "v";
    private final static String YT_THUMBNAIL = "mqdefault.jpg";
    private final static String YT_APP_INTENT = "vnd.youtube:";

    /*
        Build the Url of the movie's trailer to be played by browser
    */
    public static Uri buildYoutubeVideoUri(String trailerKey){
        return Uri.parse(HTTPS + VIDEO_REQUEST_PRE + YT_BASE_URL).buildUpon()
                .appendPath(YT_VIDEO_PATH)
                .appendQueryParameter(YT_VIDEO_KEY_PARAM, trailerKey)
                .build();
    }

    /*
        Build the Url of the movie's trailer to be played by youtube app
    */
    public static Uri buildYoutubeVideoAppUri(String trailerKey){
        return Uri.parse(YT_APP_INTENT + trailerKey).buildUpon().build();
    }

    /*
        Build the "Url" getting the youtube thumbnails of trailers.
    */
    public static String buildYoutubeThumbnailUrl(String trailerKey){
        Uri builtUri = Uri.parse(HTTPS + THUMBNAIL_REQUEST_PRE + YT_BASE_URL).buildUpon()
                .appendPath(YT_THUMBNAIL_PATH)
                .appendPath(trailerKey)
                .appendPath(YT_THUMBNAIL)
                .build();

        return "" + builtUri.toString();
    }

    /*
        Simply get and read the whole http response and return the string of the whole response.
     */
    public static String getHttpResponse(URL apiRequestUrl) {
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) apiRequestUrl.openConnection();

            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.e("IO exception", e.getMessage());
            return "";
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
    }
}
