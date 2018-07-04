package aueb.gr.nasiakouts.popularmovies.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import aueb.gr.nasiakouts.popularmovies.Models.Movie;
import aueb.gr.nasiakouts.popularmovies.Adapters.MoviesGridviewAdapter;
import aueb.gr.nasiakouts.popularmovies.R;
import aueb.gr.nasiakouts.popularmovies.Utils.NetworkUtils;
import aueb.gr.nasiakouts.popularmovies.Models.PopularMoviesResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.res.Configuration;

public class MainActivity extends AppCompatActivity {

    private final static int POPULARITY_BUTTON = 1;
    private final static int RATING_BUTTON = 2;

    private final static String SORTED_BY_POPULARITY_PATH = "popular";
    private final static String SORTED_BY_RATING_PATH = "top_rated";

    @BindView(R.id.sort_by_popularity_button)
    Button popularityButton;

    @BindView(R.id.sort_by_rating_button)
    Button ratingButton;

    @BindView(R.id.connectivity_layout)
    LinearLayout connectivityProblem;

    @BindView(R.id.movies_gridview)
    GridView gridView;

    @BindView(R.id.progress_bar)
    ProgressBar pb;

    private ArrayList<Movie> movies = null;
    private MoviesGridviewAdapter gridviewAdapter = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String sortOptionSelected = sharedPreferences.getString(getString(R.string.shared_pref_sort_key), getString(R.string.sort_by_popularity));

        movies = new ArrayList<>();
        gridviewAdapter = new MoviesGridviewAdapter(this, R.layout.grid_item, movies, gridView, (FrameLayout) findViewById(R.id.grid_item_root));
        gridView.setAdapter(gridviewAdapter);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridView.setNumColumns(2);
        } else {
            gridView.setNumColumns(4);
        }

        if (sortOptionSelected.equals(getString(R.string.sort_by_popularity))) {
            FetchPopularMovies();
        } else {
            FetchTopRatedMovies();
        }
    }

    @OnClick(R.id.sort_by_popularity_button)
    public void popularityButtonClicked() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.shared_pref_sort_key), getString(R.string.sort_by_popularity));
        editor.apply();
        FetchPopularMovies();
    }

    @OnClick(R.id.sort_by_rating_button)
    public void ratingButtonClicked() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.shared_pref_sort_key), getString(R.string.sort_by_rating));
        editor.apply();
        FetchTopRatedMovies();
    }

    @OnClick(R.id.try_again)
    public void refresh() {
        connectivityProblem.setVisibility(View.GONE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sortOptionSelected = sharedPreferences.getString(getString(R.string.shared_pref_sort_key), getString(R.string.sort_by_popularity));
        if (sortOptionSelected.equals(getString(R.string.sort_by_popularity))) {
            FetchPopularMovies();
        } else {
            FetchTopRatedMovies();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.info) {
            Intent openInfoAcitivity = new Intent(this, InfoActivity.class);
            startActivity(openInfoAcitivity);
        }
        return super.onOptionsItemSelected(item);
    }

    /*
                According to which button is selected, we want to change their appearance accordingly.
             */
    private void SetSortButtonsColor(int buttonNum) {
        switch (buttonNum) {
            case POPULARITY_BUTTON:
                popularityButton.setBackground(getResources().getDrawable(R.drawable.left_circular_pink));
                ratingButton.setBackground(getResources().getDrawable(R.drawable.right_circular_grey));
                break;
            case RATING_BUTTON:
                popularityButton.setBackground(getResources().getDrawable(R.drawable.left_circular_grey));
                ratingButton.setBackground(getResources().getDrawable(R.drawable.right_circular_pink));
                break;
        }
    }

    private void FetchPopularMovies() {
        SetSortButtonsColor(POPULARITY_BUTTON);
        new FetchMoviesTask().execute(SORTED_BY_POPULARITY_PATH);
    }

    private void FetchTopRatedMovies() {
        SetSortButtonsColor(RATING_BUTTON);
        new FetchMoviesTask().execute(SORTED_BY_RATING_PATH);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            movies.clear();
            connectivityProblem.setVisibility(View.GONE);
            gridviewAdapter.clear();
            pb.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);

            super.onPreExecute();
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            URL exploreMoviesUrl = NetworkUtils.buildSortUrl(params[0]);

            try {
                String jsonExploreMoviesResponse = NetworkUtils
                        .getHttpResponse(exploreMoviesUrl);

                Gson gson = new GsonBuilder().create();
                PopularMoviesResponse moviesResponse = gson.fromJson(jsonExploreMoviesResponse, PopularMoviesResponse.class);
                movies.addAll(Arrays.asList(moviesResponse.getMovies()));
                return movies;

            } catch (Exception e) {
                Log.e("Fetching data error", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies != null) {
                gridviewAdapter.notifyDataSetChanged();
                pb.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
            } else {
                pb.setVisibility(View.GONE);
                connectivityProblem.setVisibility(View.VISIBLE);
            }
        }
    }
}
