package aueb.gr.nasiakouts.popularmovies.Models;

import com.google.gson.annotations.SerializedName;

/*
    The spoken language of the movies
 */
public class SpokenLanguage {
    @SerializedName("iso_639_1")
    private String iso639_1;

    private String name;

    public String getIso6391() {
        return iso639_1;
    }

    public void setIso6391(String value) {
        this.iso639_1 = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getNameWithFirstLetterUppercase() {
        if(name.length() == 0) return iso639_1.toUpperCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
