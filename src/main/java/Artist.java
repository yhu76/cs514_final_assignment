import java.util.ArrayList;

public class Artist {

    protected String artist_name;
    protected int artist_id;

    public Artist(int artist_id, String artist_name) {
        this.artist_id = artist_id;
        this.artist_name = artist_name.trim();
    }

    public String toSQL() {
        return "insert into artists (id, name) values (" + this.artist_id + ", \"" + this.artist_name + "\");";
    }

}
