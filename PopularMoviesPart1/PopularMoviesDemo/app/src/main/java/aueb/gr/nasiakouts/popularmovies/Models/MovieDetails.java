package aueb.gr.nasiakouts.popularmovies.Models;

import com.google.gson.annotations.SerializedName;

import aueb.gr.nasiakouts.popularmovies.Utils.TransformUtils;

public class MovieDetails {

    private final static String BASE_URL = "http://image.tmdb.org/t/p/";
    private final static String POSTER_SIZE = "w342";
    private final static String BACKDROP_SIZE = "w780";

    private long id;

    @SerializedName("backdrop_path")
    private String backdropPath;

    private Genre[] genres;

    @SerializedName("original_language")
    private String originalLanguage;

    @SerializedName("original_title")
    private String originalTitle;

    private String overview;

    private double popularity;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("belongs_to_collection")
    private MovieCollection collection;

    private long runtime;

    @SerializedName("spoken_languages")
    private SpokenLanguage[] spokenLanguages;

    private String title;

    private boolean video;

    @SerializedName("vote_average")
    private double avgVote;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public long getRuntime() {
        return runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getAvgVote() {
        return avgVote;
    }

    public void setAvgVote(double avgVote) {
        this.avgVote = avgVote;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Genre[] getGenres() {
        return genres;
    }

    public void setGenres(Genre[] genres) {
        this.genres = genres;
    }

    public SpokenLanguage[] getSpokenLanguages() {
        return spokenLanguages;
    }

    public void setSpokenLanguages(SpokenLanguage[] spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    public String getBackdropUrl() {
        if (backdropPath == null) return null;
        return BASE_URL + BACKDROP_SIZE + backdropPath;
    }

    public String getFullPosterUrl() {
        if (posterPath == null) return null;
        return BASE_URL + POSTER_SIZE + posterPath;
    }

    public String getReleaseYear() {
        return releaseDate.substring(0, releaseDate.indexOf("-"));
    }

    public String getMoreLanguagesFullString() {
        if (spokenLanguages == null || spokenLanguages.length == 0) return "";
        StringBuilder moreLanguages = new StringBuilder("");
        Boolean notFirst = false;
        for (SpokenLanguage language : spokenLanguages) {
            if (language != null && (!language.toString().trim().equals(""))) {
                if (notFirst) moreLanguages.append(", ");
                moreLanguages.append(language.getNameWithFirstLetterUppercase());
                notFirst = true;
            }
        }
        return moreLanguages.toString();
    }

    public String getGenresFullString() {
        if (genres == null || genres.length == 0) return "";
        StringBuilder genresFull = new StringBuilder("");
        Boolean notFirst = false;
        for (Genre genre : genres) {
            if (genre != null && (!genre.toString().trim().equals(""))) {
                if (notFirst) genresFull.append(", ");
                genresFull.append(genre.getName());
                notFirst = true;
            }
        }
        return genresFull.toString();
    }

    public String getReleaseDateModified() {
        return TransformUtils.getFriendlyDate(releaseDate);
    }

    public MovieCollection getCollection() {
        return collection;
    }

    public void setCollection(MovieCollection collection) {
        this.collection = collection;
    }

    public String getCollectionInfo() {
        return collection == null ? "Standalone" : collection.getName() + " Collection";
    }
}
