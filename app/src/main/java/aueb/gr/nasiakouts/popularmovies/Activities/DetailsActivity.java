package aueb.gr.nasiakouts.popularmovies.Activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import aueb.gr.nasiakouts.popularmovies.databinding.ActivityDetailsBinding;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CirclePageIndicator;
import com.synnapps.carouselview.ViewListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aueb.gr.nasiakouts.popularmovies.Adapters.ReviewsAdapter;
import aueb.gr.nasiakouts.popularmovies.Data.FavoriteMovieContract;
import aueb.gr.nasiakouts.popularmovies.Fragments.FavoriteFragment;
import aueb.gr.nasiakouts.popularmovies.Models.MovieDetails;
import aueb.gr.nasiakouts.popularmovies.Models.Review;
import aueb.gr.nasiakouts.popularmovies.Models.ReviewsResponse;
import aueb.gr.nasiakouts.popularmovies.Models.VideosInfo;
import aueb.gr.nasiakouts.popularmovies.Models.VideosResponse;
import aueb.gr.nasiakouts.popularmovies.R;
import aueb.gr.nasiakouts.popularmovies.Utils.NetworkUtils;
import aueb.gr.nasiakouts.popularmovies.Utils.TransformUtils;

import static aueb.gr.nasiakouts.popularmovies.Data.FavoriteMovieContract.FavoriteMovieEntry.*;

public class DetailsActivity extends AppCompatActivity {

    private ActivityDetailsBinding binding;

    private String setOfExtras;
    private MovieDetails movieDetails;
    private ReviewsAdapter reviewsAdapter;
    private List<Review> reviewList = new ArrayList<>();
    private Parcelable reviewsRvState;

    private static final int DETAILS_LOADER_USING_API = 43;
    private static final int DETAILS_LOADER_USING_DB = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.detailsToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        reviewsAdapter = new ReviewsAdapter(reviewList);
        binding.reviewsRv.setAdapter(reviewsAdapter);
        binding.reviewsRv.setLayoutManager(new LinearLayoutManager(this));
        binding.reviewsRv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        binding.reviewsRv.setNestedScrollingEnabled(true);

        if(savedInstanceState != null) {
            reviewsRvState = savedInstanceState.getParcelable("ListState");
        }

        /*
            extras[0] -> selected movie id
            extras[1] -> sorted option selected
         */
        Intent intentStartedThisActivity = getIntent();
        if (intentStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            setOfExtras = intentStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
        }
        if (setOfExtras != null && !setOfExtras.equals("")) {
            fetchCorrespondingData(setOfExtras);
        }
        binding.favFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRemoveFavorite();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ListState", binding.reviewsRv.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.reviewsRv.setNestedScrollingEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
            If the share icon is pressed then the first trailer on youtube available
            is going to be shared. The user will choose on which up
            via the chooser
            We make sure there is valid trailer available
            and that there is internet connection before displaying the chooser
         */
        if (item.getItemId() == R.id.share) {

            ConnectivityManager connectivityManager =
                    (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

            if(connectivityManager != null &&
                    (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                            .getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                            .getState() == NetworkInfo.State.CONNECTED)){
                if(movieDetails.isThereValidTrailer()){
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "" +
                            NetworkUtils.buildYoutubeVideoUri(movieDetails.getTrailers()[0].getKey()));
                    Intent chooser = Intent.createChooser(shareIntent, getString(R.string.share_chooser_title));

                    if (shareIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(chooser);
                    }

                }
                else {
                    Toast.makeText(this, getString(R.string.no_trailer_to_share), Toast.LENGTH_LONG).show();
                }

            }
            else {
                Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //@OnClick(R.id.try_again)
    public void refresh() {
        binding.detailsConnectivityLayout.getRoot().setVisibility(View.GONE);
        if (setOfExtras != null && !setOfExtras.equals("")) {
            fetchCorrespondingData(setOfExtras);
        }
    }

    private void fetchCorrespondingData(String info) {
        /*
            extras[0] -> selected movie id
            extras[1] -> sorted option selected
         */
        String[] extras = info.split(",");
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.movie_id), extras[0]);

        LoaderManager loaderManager = this.getSupportLoaderManager();
        if (extras[1].equals(getString(R.string.sort_by_popularity)) || extras[1].equals(getString(R.string.sort_by_rating))) {
            Loader<String> apiFetchLoader = loaderManager.getLoader(DETAILS_LOADER_USING_API);

            if (apiFetchLoader == null) {
                loaderManager.initLoader(DETAILS_LOADER_USING_API, bundle, detailsApiLoader);
            } else {
                loaderManager.restartLoader(DETAILS_LOADER_USING_API, bundle, detailsApiLoader);
            }

        } else if (extras[1].equals(getString(R.string.fav))) {
            Loader<String> favLoader = loaderManager.getLoader(DETAILS_LOADER_USING_DB);

            if (favLoader == null) {
                loaderManager.initLoader(DETAILS_LOADER_USING_DB, bundle, detailsDBLoader);
            } else {
                loaderManager.restartLoader(DETAILS_LOADER_USING_DB, bundle, detailsDBLoader);
            }
        }
    }

    private final int IS_FAVED = 0;
    private final int IS_NOT_FAVED = 1;

    private void addRemoveFavorite(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String[] extras = setOfExtras.split(",");

        // check to see if the movie has been of favorites
        Cursor cursor = getContentResolver().query(
                FavoriteMovieContract.FavoriteMovieEntry.buildFlavorsUri(Long.parseLong(extras[0])),
                null,null,null,null );

        // if there wasn't on favorites already, it means the user added just now
        if(cursor != null && cursor.getCount() <= 0){
            cursor.close();
            binding.favFab.setImageResource(R.drawable.ic_favorite);
            editor.putInt(getString(R.string.sharedprefisFaved)+ movieDetails.getId(), IS_FAVED);
            editor.apply();

            ContentValues contentValues = new ContentValues();
            // Put all details except reviews into the ContentValues
            contentValues.put(MOVIE_ID, movieDetails.getId());
            contentValues.put(TITLE, movieDetails.getTitle());
            contentValues.put(BACKDROP_IMAGE, movieDetails.getBackdropPath());
            contentValues.put(GENRES_ID, movieDetails.getGenresIdFullString());
            contentValues.put(GENRES_NAME, movieDetails.getGenresFullString());
            contentValues.put(ORIGINAL_LANGUAGE, movieDetails.getOriginalLanguage());
            contentValues.put(SPOKEN_LANGUAGES, movieDetails.getMoreLanguagesFullString());
            contentValues.put(PLOT_SUMMARY, movieDetails.getOverview());
            contentValues.put(POPULARITY, movieDetails.getPopularity());
            contentValues.put(POSTER_IMAGE, movieDetails.getPosterPath());
            contentValues.put(RELEASE_DATE, movieDetails.getReleaseDate());
            contentValues.put(COLLECTION, movieDetails.getCollectionInfo());
            contentValues.put(LENGTH, movieDetails.getRuntime());
            contentValues.put(RATING, movieDetails.getAvgVote());
            contentValues.put(TRAILER_KEY, movieDetails.getTrailerKeysFullString());
            contentValues.put(TRAILER_NAME, movieDetails.getTrailerNamesFullString());

            // Insert the content values via a ContentResolver
            getContentResolver().insert(FavoriteMovieContract.FavoriteMovieEntry.FAVORITE_MOVIE_URI, contentValues);
        }
        // if the movie was already on favorites, it means the user now removed it
        else{
            if(cursor != null) cursor.close();
            binding.favFab.setImageResource(R.drawable.ic_favorite_border);
            editor.remove(getString(R.string.sharedprefisFaved) + movieDetails.getId());
            editor.apply();
            getContentResolver().delete(FavoriteMovieContract.FavoriteMovieEntry.buildFlavorsUri(Long.parseLong(extras[0])), null, null);

            /*
                used to inform the favorite fragment that when the user resumes to it
                it has to refresh the RecyclerView of favorites
             */
            FavoriteFragment.foundChanges = true;
        }
    }

    private void populateUi(MovieDetails details) {

        // show full or outline fav icon depending on the movie
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int isFavorite = sharedPreferences.getInt(getString(R.string.sharedprefisFaved) + details.getId(), IS_NOT_FAVED);
        if(isFavorite == IS_FAVED) {
            binding.favFab.setImageResource(R.drawable.ic_favorite);
        } else {
            binding.favFab.setImageResource(R.drawable.ic_favorite_border);
        }

        binding.detailsCollapsingToolbar.setTitle(details.getTitle());

        if (details.getBackdropUrl() == null) {
            binding.detailsBackdropImage.setImageResource(R.drawable.gradient_gv_item_border);
        } else {
            int[] dimens = TransformUtils.calculateDimens(this, TransformUtils.BACKDROP_IMAGE_TRANSFORMATION, null, -1);
            Picasso.get()
                .load(details.getBackdropUrl()).resize(dimens[0], dimens[1])
                .error(R.drawable.gradient_gv_item_border)
                .into(binding.detailsBackdropImage);

            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) binding.detailsAppbar.getLayoutParams();
            lp.width = dimens[0];
            lp.height = dimens[1];
            binding.detailsAppbar.setLayoutParams(lp);

            LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) binding.detailsCollapsingToolbar.getLayoutParams();
            lp2.width = dimens[0];
            lp2.height = dimens[1];
            binding.detailsCollapsingToolbar.setLayoutParams(lp2);
        }

        // in case we are on landscape mode then we set the toolbar collapsed by default in order to avoid taking up the whole screen
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            binding.detailsAppbar.setExpanded(false, true);
        }

        if (details.getFullPosterUrl() == null) {
            binding.detailsPoster.setImageResource(R.drawable.no_image_available);
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                int[] dimens = TransformUtils.calculateDimens(this, TransformUtils.POSTER_TRANSFORMATION_DETAIL_VIEW, null, -1);
                Picasso.get()
                    .load(details.getFullPosterUrl())
                    .error(R.drawable.no_image_available)
                    .resize(dimens[0], dimens[1])
                    .into(binding.detailsPoster);
            } else {
                int[] dimens = TransformUtils.calculateDimens(this, TransformUtils.POSTER_TRANSFORMATION_DETAIL_VIEW_LAND, null, -1);
                Picasso.get()
                    .load(details.getFullPosterUrl())
                    .error(R.drawable.no_image_available)
                    .resize(dimens[0], dimens[1])
                    .into(binding.detailsPoster);
            }
        }

        binding.detailsRatingBar.setRating((float) (details.getAvgVote() / 2.0));

        binding.detailsAvg.setText("" + details.getAvgVote());

        binding.popularity.setText("" + (int) details.getPopularity());

        binding.collection.setText(details.getCollectionInfo());

        binding.detailsReleaseYear.setText(details.getReleaseDateModified());

        binding.length.setText("" + details.getRuntime());

        binding.spokenLanguages.setText(details.getMoreLanguagesFullString());

        binding.genre.setText(details.getGenresFullString());

        binding.plot.setText(details.getOverview());

        /*
            Populate trailer's carousel view
         */
        final VideosInfo[] trailers = details.getTrailers();
        if(trailers != null && binding.trailerCarousel != null){
            binding.trailerCarousel.setViewListener(new ViewListener() {
                @Override
                public View setViewForPosition(final int position) {
                    View view = getLayoutInflater().inflate(R.layout.trailer_carousel, null);

                    ImageView image = view.findViewById(R.id.carousel_trailer_thumbnail);
                    Picasso.get()
                            .load(NetworkUtils.buildYoutubeThumbnailUrl(trailers[position].getKey()))
                            .fit()
                            .centerCrop()
                            .error(R.drawable.no_image_available)
                            .into(image);

                    TextView text = view.findViewById(R.id.carousel_trailer_name);
                    text.setText(trailers[position].getName());

                    binding.trailerCarousel.setIndicatorGravity(Gravity.CENTER | Gravity.BOTTOM);

                    ImageView playTrailer = view.findViewById(R.id.play_trailer);
                    playTrailer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            playTrailer(trailers[position].getKey());
                        }
                    });

                    return view;
                }
            });
            binding.trailerCarousel.setPageCount(trailers.length);
        }
        if(!details.isThereValidTrailer()) {
            binding.trailerCarousel.setVisibility(View.GONE);
        }
        else if(trailers != null && trailers.length == 1){
            CirclePageIndicator indicator = binding.trailerCarousel.findViewById(R.id.indicator);
            if(indicator !=null) indicator.setVisibility(View.GONE);
        }

        /* --------------------------------- */

        if (details.getReviews() == null || details.getReviews().length == 0) {
            reviewList.clear();
            Review stubReview = new Review();
            stubReview.setContent("no review available yet");
            reviewList.add(stubReview);
            reviewsAdapter.notifyDataSetChanged();
        }
        else if(details.getReviews() != null) {
            reviewList.clear();
            reviewList.addAll(Arrays.asList(details.getReviews()));
            reviewsAdapter.notifyDataSetChanged();
            resetReviewsRecyclerViewHeight();
            binding.reviewsRv.getLayoutManager().onRestoreInstanceState(reviewsRvState);
        }
    }

    /*
        Creates an intent to play the trailer, whose key was passed as param
        Checking for internet connection first
        Trying to start activity using the intent specified for the youtube's app
        In case this fails, starts activity using a regular URL intent (open by browsers)
     */
    private void playTrailer(String trailerKey){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

        if(connectivityManager != null &&
                (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                        .getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                        .getState() == NetworkInfo.State.CONNECTED)) {

            Intent appIntent = new Intent(Intent.ACTION_VIEW, NetworkUtils.buildYoutubeVideoAppUri(trailerKey));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, NetworkUtils.buildYoutubeVideoUri(trailerKey));
            try {
                this.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                this.startActivity(webIntent);
            }
        }
        else {
            Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    /*
        In case the height (by default wrap content)
        is larger than 500, we set it to 500 and make the
        recycler view be scrollable
     */
    private void resetReviewsRecyclerViewHeight(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        ViewTreeObserver vto = binding.reviewsRv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.reviewsRv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int height = binding.reviewsRv.getMeasuredHeight();
                if(height > 500) {
                    binding.reviewsRv.setNestedScrollingEnabled(true);
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) binding.reviewsRv.getLayoutParams();
                    lp.height = 500;
                    binding.reviewsRv.setLayoutParams(lp);
                }
            }
        });
    }

    private void prepareFetching(){
        binding.detailsProgressBar.setVisibility(View.VISIBLE);
        binding.detailsNestedScrollview.setVisibility(View.INVISIBLE);
    }

    private void displayDetails(MovieDetails details){
        populateUi(details);
        movieDetails = details;
        binding.detailsNestedScrollview.setVisibility(View.VISIBLE);
        binding.detailsLinearLayout.setVisibility(View.VISIBLE);
        binding.detailsConnectivityLayout.getRoot().setVisibility(View.GONE);
        binding.reviewsRv.setVisibility(View.VISIBLE);
    }

    @SuppressLint("Range")
    private MovieDetails getMovieDetailsObjectFromCursor(Cursor data){
        if(data == null)
            return null;

        data.moveToFirst();

        String movieId = data.getString(data
                .getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.MOVIE_ID));

        String title = data.getString(data
                .getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.TITLE));

        String releaseDate = data.getString(data
                .getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.RELEASE_DATE));

        String poster = data.getString(data
                .getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.POSTER_IMAGE));

        String popularity = data.getString(data
                .getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.POPULARITY));

        String landscapePoster = data.getString(data
                .getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.BACKDROP_IMAGE));

        String genresId = data.getString(data
                .getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.GENRES_ID));

        String genresNames = data.getString(data
                .getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.GENRES_NAME));

        String originalLan = data.getString(data
                .getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.ORIGINAL_LANGUAGE));

        String spokenLan = data.getString(data
                .getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.SPOKEN_LANGUAGES));

        String plot = data.getString(data
                .getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.PLOT_SUMMARY));

        String collection = data.getString(data
                .getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLLECTION));

        String length = data.getString(data
                .getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.LENGTH));

        String rating = data.getString(data
                .getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.RATING));

        String trailerKeys = data.getString(data
                .getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.TRAILER_KEY));

        String trailerNames = data.getString(data
                .getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.TRAILER_NAME));

        return new MovieDetails(movieId, title, poster, popularity, releaseDate,
                landscapePoster, genresId, genresNames, originalLan, spokenLan,
                plot, collection, length, rating, trailerKeys, trailerNames);
    }

    // ----------------- LOADERS ----------------
    private LoaderManager.LoaderCallbacks<MovieDetails> detailsApiLoader
            = new LoaderManager.LoaderCallbacks<MovieDetails>() {

        @Override
        public Loader<MovieDetails> onCreateLoader(int id, @Nullable Bundle args) {
            if (args == null) return null;
            final String movieId = args.getString(getString(R.string.movie_id));
            if(movieId == null)
                return null;

            return new AsyncTaskLoader<MovieDetails>(DetailsActivity.this) {
                MovieDetails cachedMovieDetails = null;

                @Override
                protected void onStartLoading() {
                    if(cachedMovieDetails == null){
                        prepareFetching();
                        forceLoad();
                    } else {
                        deliverResult(cachedMovieDetails);
                    }
                }

                @Override
                public MovieDetails loadInBackground() {
                    URL movieDetailsUrl = NetworkUtils.buildDetailsUrl(movieId);

                    try {
                        String jsonMovieDetailsResponse = NetworkUtils
                                .getHttpResponse(movieDetailsUrl);

                        Gson gson = new GsonBuilder().create();
                        MovieDetails selectedMovie = gson.fromJson(jsonMovieDetailsResponse, MovieDetails.class);


                        URL movieTrailersUrl = NetworkUtils.buildGetVideosUrl(movieId);

                        try {
                            String jsonMovieTrailersResponse = NetworkUtils
                                    .getHttpResponse(movieTrailersUrl);

                            gson = new GsonBuilder().create();
                            VideosResponse videos = gson.fromJson(jsonMovieTrailersResponse, VideosResponse.class);
                            selectedMovie.setTrailers(videos.getOnlyTrailersOnYoutube());
                        } catch (Exception e) {
                            Log.e("Fetching video error", e.getMessage());
                            return selectedMovie;
                        }

                        URL movieReviewsUrl = NetworkUtils.buildGetReviewsUrl(movieId);

                        try {
                            String jsonMovieReviewsResponse = NetworkUtils
                                    .getHttpResponse(movieReviewsUrl);

                            gson = new GsonBuilder().create();
                            ReviewsResponse reviewsResponse = gson.fromJson(jsonMovieReviewsResponse, ReviewsResponse.class);
                            selectedMovie.setReviews(reviewsResponse.getReviews());
                        } catch (Exception e) {
                            Log.e("Fetching reviews error", e.getMessage());
                            return selectedMovie;
                        }

                        return selectedMovie;

                    } catch (Exception e) {
                        Log.e("Fetching data error", e.getMessage());
                        return null;
                    }
                }

                @Override
                public void deliverResult(@Nullable MovieDetails data) {
                    cachedMovieDetails = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<MovieDetails> loader, MovieDetails data) {
            binding.detailsProgressBar.setVisibility(View.GONE);
            if (data == null) {
                binding.detailsLinearLayout.setVisibility(View.GONE);
                binding.detailsConnectivityLayout.getRoot().setVisibility(View.VISIBLE);
                binding.detailsNestedScrollview.setVisibility(View.GONE);
                binding.detailsCollapsingToolbar.setTitle(getString(R.string.no_internet));
            } else {
                displayDetails(data);
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<MovieDetails> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<Cursor> detailsDBLoader
            = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            prepareFetching();

            if(args == null) return null;
            String movieId = args.getString(getString(R.string.movie_id));
            if(movieId == null || movieId.length() == 0) return null;

            return new CursorLoader(DetailsActivity.this,
                    FavoriteMovieContract.FavoriteMovieEntry.buildFlavorsUri(Long.parseLong(movieId)),
                    null,null, null, null);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            MovieDetails movie = null;
            if(movieDetails != null) {
                movie = movieDetails;
            }
            else if(data != null) {
                movie = getMovieDetailsObjectFromCursor(data);
            }

            if(data != null) data.close();

            binding.detailsProgressBar.setVisibility(View.GONE);

            if (movie == null) {
                binding.detailsLinearLayout.setVisibility(View.GONE);
                binding.detailsConnectivityLayout.getRoot().setVisibility(View.VISIBLE);
                binding.detailsNestedScrollview.setVisibility(View.GONE);
                binding.detailsCollapsingToolbar.setTitle(getString(R.string.no_internet));
            } else {
                displayDetails(movie);
                Toast.makeText(getApplicationContext(),"No reviews display on favorites",Toast.LENGTH_SHORT).show();
                binding.reviewsRv.setVisibility(View.GONE);
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        }
    };
    // ------------------------------------------
}
