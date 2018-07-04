package aueb.gr.nasiakouts.popularmovies.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.net.URL;

import aueb.gr.nasiakouts.popularmovies.Models.MovieDetails;
import aueb.gr.nasiakouts.popularmovies.R;
import aueb.gr.nasiakouts.popularmovies.Utils.NetworkUtils;
import aueb.gr.nasiakouts.popularmovies.Utils.TransformUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.details_rating_bar)
    RatingBar ratingBar;

    @BindView(R.id.details_backdrop_image)
    ImageView backdrop;

    @BindView(R.id.details_avg)
    TextView avg;

    @BindView(R.id.detailsReleaseYear)
    TextView releaseDate;

    @BindView(R.id.length)
    TextView length;

    @BindView(R.id.plot)
    TextView plot;

    @BindView(R.id.details_collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.details_toolbar)
    Toolbar toolbar;

    @BindView(R.id.details_appbar)
    AppBarLayout appBar;

    @BindView(R.id.details_poster)
    ImageView poster;

    @BindView(R.id.popularity)
    TextView popularity;

    @BindView(R.id.collection)
    TextView collection;

    @BindView(R.id.spoken_languages)
    TextView moreLanguages;

    @BindView(R.id.genre)
    TextView genre;

    @BindView(R.id.details_linear_layout)
    LinearLayout detailsLinear;

    @BindView(R.id.details_connectivity_layout)
    LinearLayout connectivityProblem;

    @BindView(R.id.details_progress_bar)
    ProgressBar pb;

    @BindView(R.id.details_nested_scrollview)
    NestedScrollView nestedScrollView;

    private String selectedMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intentStartedThisActivity = getIntent();

        if (intentStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            selectedMovieId = intentStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
        }

        if (!selectedMovieId.equals("")) {
            new FetchMovieDetailsTask().execute(this, selectedMovieId);
        }
    }

    @OnClick(R.id.try_again)
    public void refresh() {
        connectivityProblem.setVisibility(View.GONE);
        if (!selectedMovieId.equals("")) {
            new FetchMovieDetailsTask().execute(this, selectedMovieId);
        }
    }

    @SuppressLint("SetTextI18n")
    private void populateUi(MovieDetails details) {
        collapsingToolbarLayout.setTitle(details.getTitle());

        if (details.getBackdropUrl() == null) {
            backdrop.setImageResource(R.drawable.gradient_gv_item_border);
        } else {
            int[] dimens = TransformUtils.calculateDimens(this, TransformUtils.BACKDROP_IMAGE_TRANSFORMATION, null);
            Picasso.with(this).load(details.getBackdropUrl()).resize(dimens[0], dimens[1]).into(backdrop);

            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBar.getLayoutParams();
            lp.width = dimens[0];
            lp.height = dimens[1];
            appBar.setLayoutParams(lp);

            LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
            lp2.width = dimens[0];
            lp2.height = dimens[1];
            collapsingToolbarLayout.setLayoutParams(lp2);

        }
        if (details.getFullPosterUrl() == null) {
            poster.setImageResource(R.drawable.no_image_available);
        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                int[] dimens = TransformUtils.calculateDimens(this, TransformUtils.POSTER_TRANSFORMATION_DETAIL_VIEW, null);
                Picasso.with(this).load(details.getFullPosterUrl()).resize(dimens[0], dimens[1]).into(poster);
            } else {
                int[] dimens = TransformUtils.calculateDimens(this, TransformUtils.POSTER_TRANSFORMATION_DETAIL_VIEW_LAND, null);
                Picasso.with(this).load(details.getFullPosterUrl()).resize(dimens[0], dimens[1]).into(poster);
            }

        }

        ratingBar.setRating((float) (details.getAvgVote() / 2.0));

        avg.setText("" + details.getAvgVote());

        popularity.setText("" + (int) details.getPopularity());

        collection.setText(details.getCollectionInfo());

        releaseDate.setText(details.getReleaseDateModified());

        length.setText("" + details.getRuntime());

        moreLanguages.setText(details.getMoreLanguagesFullString());

        genre.setText(details.getGenresFullString());

        plot.setText(details.getOverview());
    }

    public class FetchMovieDetailsTask extends AsyncTask<Object, Void, MovieDetails> {

        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.INVISIBLE);
            super.onPreExecute();
        }

        @Override
        protected MovieDetails doInBackground(Object... params) {
            if (params.length == 0) {
                return null;
            }

            URL exploreMoviesUrl = NetworkUtils.buildDetailsUrl(params[1].toString());

            try {
                String jsonExploreMoviesResponse = NetworkUtils
                        .getHttpResponse(exploreMoviesUrl);

                Gson gson = new GsonBuilder().create();
                return gson.fromJson(jsonExploreMoviesResponse, MovieDetails.class);

            } catch (Exception e) {
                Log.e("Fetching data error", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieDetails details) {
            pb.setVisibility(View.GONE);

            if (details == null) {
                detailsLinear.setVisibility(View.GONE);
                connectivityProblem.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
                collapsingToolbarLayout.setTitle(getString(R.string.no_internet));
            } else {
                populateUi(details);
                nestedScrollView.setVisibility(View.VISIBLE);
                detailsLinear.setVisibility(View.VISIBLE);
                connectivityProblem.setVisibility(View.GONE);
            }
        }
    }
}
