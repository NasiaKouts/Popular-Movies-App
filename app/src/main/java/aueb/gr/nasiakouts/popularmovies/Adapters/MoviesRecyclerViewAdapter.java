package aueb.gr.nasiakouts.popularmovies.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import aueb.gr.nasiakouts.popularmovies.databinding.GridItemBinding;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import aueb.gr.nasiakouts.popularmovies.Activities.DetailsActivity;
import aueb.gr.nasiakouts.popularmovies.Models.Movie;
import aueb.gr.nasiakouts.popularmovies.R;
import aueb.gr.nasiakouts.popularmovies.Utils.TransformUtils;

/*
    This is our custom adapter that is used to fill the main RecyclerView
    Used by the RecyclerView used by the 3 fragments to display each time,
    either the popular movies either the top ratted either the user's favorites
 */
public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<Movie> moviesList;
    private String sortedBy;
    private int numberOfColumns;

    public MoviesRecyclerViewAdapter(
            Context context,
            RecyclerView recyclerView,
            ArrayList<Movie> movies
    ) {

        this.context = context;
        this.recyclerView = recyclerView;
        this.moviesList = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GridItemBinding binding = GridItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Movie currentMovie = moviesList.get(position);
        if(currentMovie == null) return;

        if (viewHolder.moviePoster.getTag() == null ||
                viewHolder.moviePoster.getDrawable() == null) {

            final String posterUrl = currentMovie.getFullPosterUrl();
            final ImageView moviePosterImageView = viewHolder.moviePoster;

            populatePoster(posterUrl, moviePosterImageView);
        }

        /*
            Depending on which sort option has the user selected,
            we display the relative info and icon at the bottom of the GridView item
         */
        TextView sortInfoTextView = viewHolder.sortInfo;
        ImageView sortIconImageView = viewHolder.sortIcon;

        final String sortOptionSelected;
        if(sortedBy == null) {
            sortOptionSelected = context.getString(R.string.sort_by_popularity);
        } else {
            sortOptionSelected = sortedBy;
        }
        if (sortOptionSelected.equals(context.getString(R.string.sort_by_popularity))
                || sortOptionSelected.equals(context.getString(R.string.fav))) {
            sortInfoTextView.setText("" + currentMovie.getPopularityInt());
            sortIconImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_local_activity));
        } else {
            sortInfoTextView.setText("" + currentMovie.getVoteAvg());
            sortIconImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star));
        }


        TextView yearTextView = viewHolder.year;
        yearTextView.setText("" + currentMovie.getReleaseYear());

        /*
            Whenever the user press a RecyclerView's item,
            the DetailsActivity with this specific movie's (GridView item's) info
            should open up on the screen
         */
        viewHolder.moviePoster.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent openDetails = new Intent(v.getContext(), DetailsActivity.class);
                openDetails.putExtra(Intent.EXTRA_TEXT, currentMovie.getId() + "," + sortOptionSelected);
                context.startActivity(openDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesList == null ? -1 : moviesList.size();
    }

    public void setNumberOfColumns(int columns){
        this.numberOfColumns = columns;
    }

    /*
        Depending on which sort case we are, we display a tiny info different
        If we are on popular or favorites we display the popularity number on
        the bottom of each item, otherwise we display the average rating
     */
    public void setSortedBy(String sortedBy){
        this.sortedBy = sortedBy;
    }

    private void populatePoster(String posterUrl, ImageView posterImageView) {
        if (posterUrl == null) {
            posterImageView.setImageResource(R.drawable.no_image_available);
            return;
        }

        int[] dimens = TransformUtils.calculateDimens(
                (Activity) context,
                TransformUtils.POSTER_TRANSFORMATION,
                recyclerView,
                numberOfColumns);

        Picasso.get()
                .load(posterUrl)
                .resize(dimens[0], dimens[1])
                .error(R.drawable.no_image_available)
                .into(posterImageView);

        //if (recyclerItemRoot != null)
        //    recyclerItemRoot.setLayoutParams(new FrameLayout.LayoutParams(dimens[0], dimens[1]));
    }

    /*
        Swap adapter's source data and notify that change has occurred
     */
    public void swap(ArrayList<Movie> newData) {
        if(newData == null || newData.size()==0)
            return;
        if(moviesList != null && moviesList.size()>0)
            moviesList.clear();
        if(moviesList == null) moviesList = new ArrayList<>();
        moviesList.addAll(newData);
        notifyDataSetChanged();

    }

    /*
        This is our ViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView moviePoster;
        TextView sortInfo;
        ImageView sortIcon;
        TextView year;

        public ViewHolder(GridItemBinding binding) {
            super(binding.getRoot());
            this.moviePoster = binding.moviePoster;
            this.sortInfo = binding.sortInfo;
            this.sortIcon = binding.gridItemSortIcon;
            this.year = binding.releaseYear;
        }
    }
}
