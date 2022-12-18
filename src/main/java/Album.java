import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Album {

    protected Artist artist;
    protected String album_name;
    protected int album_id;

    // constructor 1
    public Album(){}

    // constructor 2
    public Album(int album_id, String album_name) {
        this.album_id = album_id;
        this.album_name = album_name.trim();
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Artist getArtist() {
        return this.artist;
    }

    public String toSQL() {
        return "insert into albums (id, name, artist) values (" + this.album_id + ", \"" + this.album_name + "\", "
                + this.artist.artist_id + ");";
    }

}
