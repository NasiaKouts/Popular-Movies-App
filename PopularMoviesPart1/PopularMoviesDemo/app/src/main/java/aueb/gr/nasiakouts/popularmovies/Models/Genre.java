package aueb.gr.nasiakouts.popularmovies.Models;

/*
    The genre of the movies
 */
public class Genre {

    private long id;
    private String name;

    public long getID() {
        return id;
    }

    public void setID(long value) {
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getNameWithFirstLetterUppercase() {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
