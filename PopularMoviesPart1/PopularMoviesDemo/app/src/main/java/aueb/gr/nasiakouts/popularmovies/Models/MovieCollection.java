package aueb.gr.nasiakouts.popularmovies.Models;

/*
    The collection that the movie is part of
 */
public class MovieCollection {

    private long id;

    private String name;

    public MovieCollection(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
