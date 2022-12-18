public class Song {
    protected int song_id;
    protected String song_name;
    protected String song_genre;
    protected Artist artist;
    protected Album album;

    // constructor
    public Song() {}
    public Song(int id, String name, String genre) {
        this.song_id = id;
        this.song_name = name;
        this.song_genre = genre;
    }
    public Song(int id, String name) {
        this.song_id = id;
        this.song_name = name;
    }


    protected void setArtist(Artist artist) {this.artist = artist;}
    protected void setAlbum(Album album) {this.album = album;}

    public String toSQL() {
        return "insert into songs (id, name, artist, album, genre) values (" + this.song_id + ", \"" + this.song_name + "\", "
               + this.artist.artist_id + ", " + this.album.album_id + ", \"" + this.song_genre + "\"" + ");";
    }

    public String songToXML() {
        return "<song id=\"" + this.song_id + "\">\n" +
                "<title>\n" + this.song_name + "\n</title>\n" +
                "<artist> id=\"" + artist.artist_id + "\">\n" + artist.artist_name + "\n</artist>\n" +
                "<album> id=\"" + album.album_id + "\">\n" + album.album_name + "\n</album>\n" +
                "<genre>\n" + this.song_genre + "\n</genre>\n" +
                "</song>\n";
    }

}
