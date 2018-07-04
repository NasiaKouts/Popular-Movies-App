package aueb.gr.nasiakouts.popularmovies.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VideosResponse {
    private final static String TRAILER = "Trailer";
    private final static String YOUTUBE ="YouTube";
    @SerializedName("id")
    private int movieId;

    @SerializedName("results")
    private VideosInfo[] videos;

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public VideosInfo[] getVideos() {
        return videos;
    }

    public void setVideos(VideosInfo[] videos) {
        this.videos = videos;
    }

    /*
        We only keep the videos which are trailers and uploaded on youtube
     */
    public VideosInfo[] getOnlyTrailersOnYoutube(){
        ArrayList<VideosInfo> trailers = new ArrayList<>();
        for(VideosInfo video : videos) {
            if(video.getType().equalsIgnoreCase(TRAILER) && video.getSite().equalsIgnoreCase(YOUTUBE)) {
                trailers.add(video);
            }
        }
        return trailers.toArray(new VideosInfo[trailers.size()]);
    }
}
