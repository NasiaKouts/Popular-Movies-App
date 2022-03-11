package aueb.gr.nasiakouts.popularmovies.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import aueb.gr.nasiakouts.popularmovies.databinding.FragmentMoviesGridBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import aueb.gr.nasiakouts.popularmovies.Activities.InfoActivity;
import aueb.gr.nasiakouts.popularmovies.Adapters.MoviesRecyclerViewAdapter;
import aueb.gr.nasiakouts.popularmovies.Data.FavoriteMovieContract;
import aueb.gr.nasiakouts.popularmovies.Models.Movie;
import aueb.gr.nasiakouts.popularmovies.Models.ExploreMoviesResponse;
import aueb.gr.nasiakouts.popularmovies.R;
import aueb.gr.nasiakouts.popularmovies.Utils.MovieItemDecoration;
import aueb.gr.nasiakouts.popularmovies.Utils.NetworkUtils;
import aueb.gr.nasiakouts.popularmovies.Utils.TransformUtils;

public class MoviesFragment extends Fragment {

    public final static String SORTED_BY_POPULARITY_PATH = "popular";
    public final static String SORTED_BY_RATING_PATH = "top_rated";

    protected FragmentMoviesGridBinding binding;

    protected static ArrayList<Movie> movies = null;
    protected MoviesRecyclerViewAdapter moviesRecyclerViewAdapter = null;
    protected Parcelable recyclerViewPosition;
    protected static boolean rotated;

    public static final int MOVIE_FETCH_USING_API = 41;
    public static final int FAVORITES_FETCH_USING_DB = 42;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMoviesGridBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);

        movies = new ArrayList<>();

        // according to the mode we set different number of columns to our RecyclerView
        int numOfColumns = 2;
        RecyclerView.LayoutManager mLayoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLayoutManager = new GridLayoutManager(getContext(), 2);
        } else {
            mLayoutManager = new GridLayoutManager(getContext(), 4);
            numOfColumns = 4;
        }
        binding.moviesRecyclerView.setLayoutManager(mLayoutManager);
        moviesRecyclerViewAdapter = new MoviesRecyclerViewAdapter(getContext(), binding.moviesRecyclerView, movies);
        moviesRecyclerViewAdapter.setNumberOfColumns(numOfColumns);
        binding.moviesRecyclerView.setAdapter(moviesRecyclerViewAdapter);
        ((SimpleItemAnimator)binding.moviesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        binding.moviesRecyclerView.addItemDecoration(new MovieItemDecoration(numOfColumns, TransformUtils.dpToPx(getContext(),4)));

        if(savedInstanceState != null) {
            recyclerViewPosition = savedInstanceState.getParcelable(getString(R.string.recycler_view_pos));
        } else {
            rotated = false;
        }

        return binding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.info) {
            Intent openInfoActivity = new Intent(getContext(), InfoActivity.class);
            startActivity(openInfoActivity);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // another approach
        /*RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager != null && layoutManager instanceof LinearLayoutManager){
            recyclerViewPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        }
        outState.putInt(getString(R.string.gridview_pos), recyclerViewPosition);*/
        outState.putParcelable(getString(R.string.recycler_view_pos), binding.moviesRecyclerView.getLayoutManager().onSaveInstanceState());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null){
            recyclerViewPosition = savedInstanceState.getParcelable(getString(R.string.recycler_view_pos));

            // another approach
            /*RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if(layoutManager != null){
                int count = layoutManager.getChildCount();
                if(recyclerViewPosition != RecyclerView.NO_POSITION && recyclerViewPosition < count){
                    layoutManager.scrollToPosition(recyclerViewPosition);
                }
            }*/
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        rotated = true;
    }

    //@OnClick(R.id.try_again)
    public void refresh() {
        binding.connectivityLayout.getRoot().setVisibility(View.GONE);

        startLoader();
    }

    public void startLoader() {}

    protected void prepareFetching(){
        movies.clear();
        moviesRecyclerViewAdapter.notifyDataSetChanged();
        binding.connectivityLayout.getRoot().setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.moviesRecyclerView.setVisibility(View.GONE);
        binding.noFavoritesLayout.setVisibility(View.GONE);
    }

    protected void showRecyclerView(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(recyclerViewPosition != null && rotated){
                    rotated = false;
                    binding.moviesRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewPosition);
                }
            }
        }, 400);

        binding.progressBar.setVisibility(View.GONE);
        binding.moviesRecyclerView.setVisibility(View.VISIBLE);
    }

    public LoaderManager.LoaderCallbacks<ArrayList<Movie>> MovieApiLoader
            = new LoaderManager.LoaderCallbacks<ArrayList<Movie>>() {

        @NonNull
        @Override
        public Loader<ArrayList<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
            final String sort;
            if(args == null) {
                sort = SORTED_BY_POPULARITY_PATH;
            } else {
                String sharedPrefKey = args.getString(getString(R.string.shared_pref_sort_key));

                if(sharedPrefKey != null){
                    sort = sharedPrefKey.equals(getString(R.string.sort_by_popularity)) ?
                            SORTED_BY_POPULARITY_PATH : SORTED_BY_RATING_PATH;
                } else {
                    sort = SORTED_BY_POPULARITY_PATH;
                }
            }

            return new AsyncTaskLoader<ArrayList<Movie>>(getContext()){

                ArrayList<Movie> moviesResult = null;

                @Override
                protected void onStartLoading() {
                    if (moviesResult != null) {
                        // Use cached data
                        deliverResult(moviesResult);
                    } else {
                        prepareFetching();
                        forceLoad();
                    }
                }

                @Nullable
                @Override
                public ArrayList<Movie> loadInBackground() {
                    URL exploreMoviesUrl = NetworkUtils.buildSortUrl(sort);
                    ArrayList<Movie> newData = new ArrayList<>();

                    try {
                        String jsonExploreMoviesResponse = NetworkUtils
                                .getHttpResponse(exploreMoviesUrl);

                        Gson gson = new GsonBuilder().create();
                        ExploreMoviesResponse moviesResponse = gson.fromJson(jsonExploreMoviesResponse, ExploreMoviesResponse.class);
                        newData.addAll(Arrays.asList(moviesResponse.getMovies()));
                        return newData;

                    } catch (Exception e) {
                        Log.e("Fetching data error", e.getMessage());
                        return null;
                    }
                }

                @Override
                public void deliverResult(@Nullable ArrayList<Movie> data) {
                    moviesResult = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
            if (data != null) {
                moviesRecyclerViewAdapter.swap(data);
                showRecyclerView();
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.connectivityLayout.getRoot().setVisibility(View.VISIBLE);
            }
        }

        // ignore
        @Override
        public void onLoaderReset(@NonNull Loader<ArrayList<Movie>> loader) {
        }
    };

    public LoaderManager.LoaderCallbacks<Cursor> FavoriteMoviesLoader
            = new LoaderManager.LoaderCallbacks<Cursor>() {

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
            prepareFetching();
            return new CursorLoader(getContext(),
                    FavoriteMovieContract.FavoriteMovieEntry.FAVORITE_MOVIE_URI,
                    new String[] {FavoriteMovieContract.FavoriteMovieEntry.MOVIE_ID,
                            FavoriteMovieContract.FavoriteMovieEntry.TITLE,
                            FavoriteMovieContract.FavoriteMovieEntry.RELEASE_DATE,
                            FavoriteMovieContract.FavoriteMovieEntry.POSTER_IMAGE,
                            FavoriteMovieContract.FavoriteMovieEntry.POPULARITY },
                    null, null, null);
        }

        @SuppressLint("Range")
        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            if(data != null) {
                ArrayList<Movie> newData = new ArrayList<>();
                while (data.moveToNext()) {
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

                    Movie movie = new Movie(movieId, title, poster, popularity, releaseDate);
                    newData.add(movie);
                }
                data.close();
                moviesRecyclerViewAdapter.swap(newData);
            }
            if (moviesRecyclerViewAdapter.getItemCount() > 0) {
                showRecyclerView();
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.noFavoritesLayout.setVisibility(View.VISIBLE);
            }
        }

        // ignore
        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        }
    };
}
