package aueb.gr.nasiakouts.popularmovies.Models;

import com.google.gson.annotations.SerializedName;

public class PopularMoviesResponse {

    @SerializedName("total_pages")
    private int totalResults;

    @SerializedName("results")
    private Movie[] movies;

    public PopularMoviesResponse(int totalResults, Movie[] movies) {
        this.totalResults = totalResults;
        this.movies = movies;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public Movie[] getMovies() {
        return movies;
    }

    public void setMovies(Movie[] movies) {
        this.movies = movies;
    }
}
