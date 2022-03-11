package aueb.gr.nasiakouts.popularmovies.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import aueb.gr.nasiakouts.popularmovies.Models.Review;
import aueb.gr.nasiakouts.popularmovies.R;

/*
    Simple Recycler View adapter
    Each item consist of 2 TextViews
    Used by the RecyclerView used for displaying movie's reviews
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsHolder>{
    private List<Review> reviewsList;

    public class ReviewsHolder extends RecyclerView.ViewHolder{
        public TextView review;
        public TextView author;

        public ReviewsHolder(View view){
            super(view);
            review = view.findViewById(R.id.review);
            author = view.findViewById(R.id.review_by);
        }
    }

    public ReviewsAdapter(List<Review> reviewsList){
        this.reviewsList = reviewsList;
    }


    @Override
    public ReviewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_rv_item, parent, false);

        return new ReviewsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewsHolder holder, int position) {
        Review review = reviewsList.get(position);
        holder.review.setText(review.getContent());
        if(review.getAuthor() == null || review.getAuthor().equals("")) holder.author.setVisibility(View.GONE);
        else holder.author.setText(review.getAuthor());
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }


}
