package aueb.gr.nasiakouts.popularmovies.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import aueb.gr.nasiakouts.popularmovies.Activities.DetailsActivity;
import aueb.gr.nasiakouts.popularmovies.Models.Movie;
import aueb.gr.nasiakouts.popularmovies.R;
import aueb.gr.nasiakouts.popularmovies.Utils.TransformUtils;

/*
    This is our custom adapter that is used to fill the main GridView
 */
public class MoviesGridviewAdapter extends ArrayAdapter<Movie> {

    private final GridView movieGridView;
    private final FrameLayout gridItemRoot;

    public MoviesGridviewAdapter(Context context, int resource, ArrayList<Movie> movies, GridView gridView, FrameLayout gridItemRoot) {
        super(context, resource, movies);
        this.movieGridView = gridView;
        this.gridItemRoot = gridItemRoot;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final Movie currentMovie = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item, parent, false);

            viewHolder = new ViewHolder((ImageView) convertView.findViewById(R.id.movie_poster), (TextView) convertView.findViewById(R.id.sort_info), (ImageView) convertView.findViewById(R.id.grid_item_sort_icon), (TextView) convertView.findViewById(R.id.release_year));

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ImageView posterImageView = viewHolder.moviePoster;
        populatePoster(currentMovie.getFullPosterUrl(), posterImageView);


        /*
            Depending on which sort option has the user selected,
            we display the relative info and icon at the bottom of the GridView item
         */
        TextView sortInfoTextView = viewHolder.sortInfo;
        ImageView sortIconImageView = viewHolder.sortIcon;

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortOptionSelected = sharedPreferences.getString(getContext().getString(R.string.shared_pref_sort_key), getContext().getString(R.string.sort_by_popularity));
        if (sortOptionSelected.equals(getContext().getString(R.string.sort_by_popularity))) {
            sortInfoTextView.setText("" + currentMovie.getPopularityInt());
            sortIconImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_local_activity_black_24dp));
        } else {
            sortInfoTextView.setText("" + currentMovie.getVoteAvg());
            sortIconImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_star_black_24dp));
        }


        TextView yearTextView = viewHolder.year;
        yearTextView.setText("" + currentMovie.getReleaseYear());

        /*
            Whenever the user press a GridView item,
            the DetailsActivity with this specific movie's (GridView item's) info
            should open up on the screen
         */
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent openDetails = new Intent(v.getContext(), DetailsActivity.class);
                openDetails.putExtra(Intent.EXTRA_TEXT, currentMovie.getId());
                getContext().startActivity(openDetails);
            }
        });
        return convertView;
    }

    private void populatePoster(String posterUrl, ImageView posterImageView) {
        if (posterUrl == null) {
            posterImageView.setImageResource(R.drawable.no_image_available);
            return;
        }
        int[] dimens = TransformUtils.calculateDimens((Activity) getContext(), TransformUtils.POSTER_TRANSFORMATION, movieGridView);
        Picasso.with(getContext()).load(posterUrl).resize(dimens[0], dimens[1]).into(posterImageView);
        if (gridItemRoot != null)
            gridItemRoot.setLayoutParams(new FrameLayout.LayoutParams(dimens[0], dimens[1]));
    }

    /*
        This is our ViewHolder
     */
    public class ViewHolder {
        ImageView moviePoster;
        TextView sortInfo;
        ImageView sortIcon;
        TextView year;

        public ViewHolder(ImageView imageView, TextView textView, ImageView sortIcon, TextView year) {
            this.moviePoster = imageView;
            this.sortInfo = textView;
            this.sortIcon = sortIcon;
            this.year = year;
        }
    }
}
