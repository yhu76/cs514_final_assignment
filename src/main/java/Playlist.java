import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Playlist {
    protected ArrayList<Song> songlist;
    public Playlist() {
        songlist = new ArrayList<Song>();
    }
    public  void addSong(Song s) {
        songlist.add(s);
    }

}
