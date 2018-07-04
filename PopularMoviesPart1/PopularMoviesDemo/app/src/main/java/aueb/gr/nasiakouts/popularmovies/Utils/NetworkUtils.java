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

    private final static String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private final static String KEY_PARAM = "api_key";
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
