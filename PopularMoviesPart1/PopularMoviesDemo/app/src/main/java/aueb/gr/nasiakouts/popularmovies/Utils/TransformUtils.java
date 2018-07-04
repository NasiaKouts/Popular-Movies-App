package aueb.gr.nasiakouts.popularmovies.Utils;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.GridView;

/*
    This class, contains methods that have to do with useful transformation
    The first one is about the poster and backdrop image's resizing.
    The second is about transforming the date received from the API, to a more user friendly format.
 */
public class TransformUtils {

    /*
        Const ints used to figure out in which case we are, and do the corresponding transformation needed.
     */
    public final static int POSTER_TRANSFORMATION = 1;
    public final static int BACKDROP_IMAGE_TRANSFORMATION = 2;
    public final static int POSTER_TRANSFORMATION_DETAIL_VIEW = 3;
    public final static int POSTER_TRANSFORMATION_DETAIL_VIEW_LAND = 4;

    /*
        Dimens of the poster image we get from the API
     */
    private final static int POSTER_WIDTH = 342;
    private final static int POSTER_HEIGHT = 513;

    /*
        Dimens of the backdrop image we get from the API
     */
    private final static int BACKDROP_IMAGE_WIDTH = 780;
    private final static int BACKDROP_IMAGE_HEIGHT = 439;

    /*
        Number of the desired imaginary columns in portrait and landscape mode,
        within the DetailsActivity basic info CardView
     */
    private final static int NUMBER_OF_IMAGINARY_COLUMNS_ON_DETAIL_VIEW = 2;
    private final static int NUMBER_OF_IMAGINARY_COLUMNS_ON_DETAIL_VIEW_LAND = 3;
    /*
        "Dictionary"
     */
    private static final String[] months = {"January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"};

    // ---------------------------------------------------------------------------

    /*
        This method is being called in 4 different cases.
        According to which one, there is a different image transformation required.
        The aim of the method is to calculate and return to the caller,
        the new dimens the image should have in the particular case.
     */
    public static int[] calculateDimens(Activity activity, int caseId, @Nullable GridView gridView) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int[] dimens = new int[2];
        dimens[0] = metrics.widthPixels;
        switch (caseId) {
            // MainActivity GridView item usage
            case POSTER_TRANSFORMATION:
                if (gridView == null) {
                    dimens[0] = 0;
                    dimens[1] = 0;
                } else {
                    dimens[0] /= gridView.getNumColumns();
                    dimens[1] = dimens[0] * POSTER_HEIGHT / POSTER_WIDTH;
                }
                break;

            // DetailsActivity collapsing toolbar usage
            case BACKDROP_IMAGE_TRANSFORMATION:
                dimens[1] = dimens[0] * BACKDROP_IMAGE_HEIGHT / BACKDROP_IMAGE_WIDTH;
                break;

            // DetailsActivity movie poster usage (portrait mode)
            case POSTER_TRANSFORMATION_DETAIL_VIEW:
                dimens[0] /= NUMBER_OF_IMAGINARY_COLUMNS_ON_DETAIL_VIEW;
                dimens[1] = dimens[0] * POSTER_HEIGHT / POSTER_WIDTH;
                break;

            // DetailsActivity movie poster usage (landscape mode)
            case POSTER_TRANSFORMATION_DETAIL_VIEW_LAND:
                dimens[0] /= NUMBER_OF_IMAGINARY_COLUMNS_ON_DETAIL_VIEW_LAND;
                dimens[1] = dimens[0] * POSTER_HEIGHT / POSTER_WIDTH;
                break;
        }
        return dimens;
    }

    /*
        This method transforms the date received from the API,
        that gets as a parameter, and has the format: yy-mm-dd,
        to a more user friendly format: month year.
     */
    public static String getFriendlyDate(String date) {
        String modifiedDate = date.substring(date.indexOf("-") + 1, date.lastIndexOf("-"));
        if (modifiedDate.startsWith("0")) {
            modifiedDate = modifiedDate.substring(1);
        }
        try {
            modifiedDate = months[Integer.valueOf(modifiedDate) - 1];
        } catch (NumberFormatException e) {
            Log.e("Date format error", e.getMessage());
            return "Unknown";
        } catch (IndexOutOfBoundsException e) {
            Log.e("Invalid month number", e.getMessage());
            return "Unknown";
        }
        return modifiedDate + " " + date.substring(0, date.indexOf("-"));
    }
}
