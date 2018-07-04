package aueb.gr.nasiakouts.popularmovies.Models;

import com.google.gson.annotations.SerializedName;

public class Movie {

    private final static String BASE_URL = "http://image.tmdb.org/t/p/";
    private final static String SIZE = "w342";

    private int id;

    @SerializedName("original_title")
    private String title;

    @SerializedName("poster_path")
    private String posterPath;

    private double popularity;

    @SerializedName("vote_average")
    private double voteAvg;

    @SerializedName("release_date")
    private String releaseDate;

    public Movie(int id, String title, String posterPath) {
        this.id = id;
        this.title = title;
        this.posterPath = BASE_URL + SIZE + posterPath;
    }

    public String getId() {
        return "" + id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getFullPosterUrl() {
        if (posterPath == null) return null;
        return BASE_URL + SIZE + posterPath;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public int getPopularityInt() {
        return (int) popularity;
    }

    public double getVoteAvg() {
        return voteAvg;
    }

    public void setVoteAvg(double voteAvg) {
        this.voteAvg = voteAvg;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseYear() {
        return releaseDate.substring(0, releaseDate.indexOf("-"));
    }
}
