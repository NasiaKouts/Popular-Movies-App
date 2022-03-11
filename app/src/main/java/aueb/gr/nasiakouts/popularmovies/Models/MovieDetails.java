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

    private VideosInfo[] trailers;

    private Review[] reviews;

    public MovieDetails(String id, String title, String posterPath, String popularity,
                        String releaseDate, String landscapePoster, String genresId,
                        String genresName, String originalLanguage, String spokenLan,
                        String plot, String collection, String length, String avgVote,
                        String trailerKeys, String trailerNames) {
        this.id = Long.parseLong(id);
        this.backdropPath = landscapePoster;
        setGenresDbUse(genresId, genresName);
        this.originalLanguage = originalLanguage;
        this.overview = plot;
        this.popularity = Double.parseDouble(popularity);
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        setCollectionInfoDbUse(collection);
        this.runtime = Long.parseLong(length);
        setMoreLanguagesDbUse(spokenLan);
        this.title = title;
        this.avgVote = Double.parseDouble(avgVote);
        setTrailersDbUse(trailerKeys, trailerNames);
    }

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
        if (spokenLanguages == null || spokenLanguages.length == 0) return originalLanguage;
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

    private void setMoreLanguagesDbUse(String languages){
        String[] spokenLang = languages.split(",");
        if(spokenLang.length <= 1) return;
        SpokenLanguage[] spokenLanguageObjects = new SpokenLanguage[spokenLang.length];
        for(int i = 0; i < spokenLang.length; i++){
            spokenLanguageObjects[i] = new SpokenLanguage();
            spokenLanguageObjects[i].setName(spokenLang[i].trim());
        }
        this.spokenLanguages = spokenLanguageObjects;
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

    public String getGenresIdFullString(){
        if (genres == null || genres.length == 0) return "";
        StringBuilder genresFull = new StringBuilder("");
        Boolean notFirst = false;
        for (Genre genre : genres) {
            if (genre != null && (!genre.toString().trim().equals(""))) {
                if (notFirst) genresFull.append(", ");
                genresFull.append(genre.getID());
                notFirst = true;
            }
        }
        return genresFull.toString();
    }

    public String getTrailerKeysFullString(){
        if (trailers == null || trailers.length == 0) return "";
        StringBuilder trailersFull = new StringBuilder("");
        Boolean notFirst = false;
        for (VideosInfo trailer : trailers) {
            if (trailer != null && (!trailer.toString().trim().equals(""))) {
                if (notFirst) trailersFull.append(", ");
                trailersFull.append(trailer.getKey());
                notFirst = true;
            }
        }
        return trailersFull.toString();
    }

    public String getTrailerNamesFullString(){
        if (trailers == null || trailers.length == 0) return "";
        StringBuilder trailersFull = new StringBuilder("");
        Boolean notFirst = false;
        for (VideosInfo trailer : trailers) {
            if (trailer != null && (!trailer.toString().trim().equals(""))) {
                if (notFirst) trailersFull.append(", ");
                trailersFull.append(trailer.getName());
                notFirst = true;
            }
        }
        return trailersFull.toString();
    }

    private void setGenresDbUse(String genresId, String genresNames){
        String[] ids = genresId.split(",");
        String[] names = genresNames.split(",");
        if(ids.length != names.length) return;
        Genre[] genres = new Genre[ids.length];
        for(int i = 0; i < ids.length; i++){
            genres[i] = new Genre(Long.parseLong(ids[i].trim()), names[i].trim());
        }
        this.genres = genres;
    }

    private void setTrailersDbUse(String trailerKeys, String trailerNames){
        String[] keys = trailerKeys.split(",");
        String[] names = trailerNames.split(",");
        if(keys.length != names.length) return;
        VideosInfo[] trailers = new VideosInfo[keys.length];
        for(int i = 0; i < keys.length; i++){
            trailers[i] = new VideosInfo(keys[i].trim(), names[i].trim());
        }
        this.trailers = trailers;
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

    private void setCollectionInfoDbUse(String collectionInfo) {
        /*
            For now we don't care about the collection's id cause we never search the rest movies in a collection,
            so we push simple -1
            todo store the real collection id for further features in future
          */
        this.collection = collectionInfo.equals("Standalone") ?
                null : new MovieCollection(-1, collectionInfo.substring(0, collectionInfo.lastIndexOf(" ")));
    }

    public VideosInfo[] getTrailers() {
        return trailers;
    }

    public void setTrailers(VideosInfo[] trailers) {
        this.trailers = trailers;
    }

    public Review[] getReviews() {
        return reviews;
    }

    public void setReviews(Review[] reviews) {
        this.reviews = reviews;
    }

    public boolean isThereValidTrailer(){
        if(trailers == null || trailers.length == 0) {
            return false;
        } else {
            if(trailers.length == 1) {
                if (trailers[0].getKey() == null ||
                        trailers[0].getKey().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }
}
