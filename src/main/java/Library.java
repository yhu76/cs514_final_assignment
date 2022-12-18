import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Library {
    protected static ArrayList<Album> albums = new ArrayList<Album>();
    protected static ArrayList<Song> songs = new ArrayList<Song>();
    protected static ArrayList<Song> recommendSongs = new ArrayList<Song>();
    protected static Song song = new Song();

    protected static Playlist p = new Playlist();

    public Library() {}

    public static void addAlbum(Album a) {albums.add(a);}
    public static void addSong(Song a) {songs.add(a);}
    public static void addRecommendSongs(Song a) {recommendSongs.add(a);}

    public static void importArtist(String artistName) {
        String requestURL_1 = "https://www.theaudiodb.com/api/v1/json/523532/searchalbum.php?s=";
        StringBuilder response_1 = new StringBuilder();
        URL u_1;
        try {
            u_1 = new URL(requestURL_1 + artistName);
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL");
            return;
        }
        try {
            URLConnection connection = u_1.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int code = httpConnection.getResponseCode();
            String message = httpConnection.getResponseMessage();
            if (code != HttpURLConnection.HTTP_OK) {
                return;
            }
            InputStream instream = connection.getInputStream();
            Scanner in = new Scanner(instream);
            while (in.hasNextLine()) {
                response_1.append(in.nextLine());
            }
        } catch (IOException e) {
            System.out.println("Error reading response");
            return;
        }
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response_1.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray jalbums = (JSONArray)jsonObject.get("album"); // get the list of all albums returned.
            for (int i = 0; i < jalbums.size(); i++) {
                JSONObject jalbum = (JSONObject) jalbums.get(i);
                Artist artist = new Artist(Integer.parseInt(jalbum.get("idArtist").toString()), jalbum.get("strArtist").toString());
                Album album1 = new Album(Integer.parseInt(jalbum.get("idAlbum").toString()), (String) jalbum.get("strAlbum"));
                album1.setArtist(artist);
                addAlbum(album1);
            }
        } catch(ParseException e) {
            System.out.println("Error parsing JSON");
        }
    }

    public static int findAlbumID(String artistName, String albumName) {
        Library l = new Library();
        l.importArtist(artistName);
        for (Album album : l.albums) {
            if (album.album_name.replace(" ", "_").toLowerCase().equals(albumName.toLowerCase())) {
                return album.album_id;
            }
        }
        return 0;
    }

    public static void importAlbum(String artistName, String albumName) {
        int albumID = findAlbumID(artistName, albumName);
        if (albumID == 0) {
            System.out.println("This album does not exist.");
        } else {
            String requestURL_1 = "https://theaudiodb.com/api/v1/json/2/track.php?m=";
            StringBuilder response_1 = new StringBuilder();
            URL u_1;
            try {
                u_1 = new URL(requestURL_1 + albumID);
            } catch (MalformedURLException e) {
                System.out.println("Malformed URL");
                return;
            }
            try {
                URLConnection connection = u_1.openConnection();
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                int code = httpConnection.getResponseCode();

                String message = httpConnection.getResponseMessage();
                if (code != HttpURLConnection.HTTP_OK) {
                    return;
                }
                InputStream instream = connection.getInputStream();
                Scanner in = new Scanner(instream);
                while (in.hasNextLine()) {
                    response_1.append(in.nextLine());
                }
            } catch (IOException e) {
                System.out.println("Error reading response");
                return;
            }
            try {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(response_1.toString());
                JSONObject jsonObject = (JSONObject) obj;
                JSONArray jsongs = (JSONArray) jsonObject.get("track");
                for (int i = 0; i < jsongs.size(); i++) {
                    JSONObject jsong = (JSONObject) jsongs.get(i);
                    Song song1 = new Song(Integer.parseInt(jsong.get("idTrack").toString()), jsong.get("strTrack").toString());
                    Artist artist1 = new Artist(Integer.parseInt(jsong.get("idArtist").toString()), jsong.get("strArtist").toString());
                    Album album1 = new Album(Integer.parseInt(jsong.get("idAlbum").toString()), (String) jsong.get("strAlbum"));
                    song1.setArtist(artist1);
                    song1.setAlbum(album1);
                    song1.album.setArtist(artist1);
                    addSong(song1);
                }
            } catch (ParseException e) {
                System.out.println("Error parsing JSON");
            }
        }
    }

    public static void importSong(String artistName, String songName) {
        String requestURL_1 = "https://theaudiodb.com/api/v1/json/523532/searchtrack.php?s=";
        String requestURL_2 = "&t=";
        StringBuilder response_1 = new StringBuilder();
        URL u_1;
        try {
            u_1 = new URL(requestURL_1 + artistName + requestURL_2 + songName);
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL");
            return;
        }
        try {
            URLConnection connection = u_1.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int code = httpConnection.getResponseCode();

            String message = httpConnection.getResponseMessage();
            if (code != HttpURLConnection.HTTP_OK) {
                return;
            }
            InputStream instream = connection.getInputStream();
            Scanner in = new Scanner(instream);
            while (in.hasNextLine()) {
                response_1.append(in.nextLine());
            }
        } catch (IOException e) {
            System.out.println("Error reading response");
            return;
        }
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response_1.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray jsongs = (JSONArray)jsonObject.get("track");
            JSONObject jsong =(JSONObject) jsongs.get(0);
            Song song1 = new Song(Integer.parseInt(jsong.get("idTrack").toString()), jsong.get("strTrack").toString(),
                                  jsong.get("strGenre").toString());
            Artist artist1 = new Artist(Integer.parseInt(jsong.get("idArtist").toString()), jsong.get("strArtist").toString());
            Album album1 = new Album(Integer.parseInt(jsong.get("idAlbum").toString()), (String) jsong.get("strAlbum"));
            song1.setArtist(artist1);
            song1.setAlbum(album1);
            song1.album.setArtist(artist1);
            song = song1;
        } catch(ParseException e) {
            System.out.println("Error parsing JSON");
        }
    }

    public static void albumsToSQL(ArrayList<Album> albums) {
        Connection connection = null;
        Statement statement;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:music.db");
            statement = connection.createStatement();
            statement.executeUpdate("drop table if exists albums;");
            statement.executeUpdate("drop table if exists artists;");
            statement.executeUpdate("drop table if exists songs;");
            statement.executeUpdate("CREATE TABLE albums (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL, artist INTEGER NOT NULL);");
            statement.executeUpdate("create table artists (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL);");
            for (Album album : albums) {
                String sql_command_1 = album.toSQL();
                statement.executeUpdate(sql_command_1);
            }
            String sql_command_2 = albums.get(0).getArtist().toSQL();
            statement.executeUpdate(sql_command_2);
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    public static void songsToSQL(ArrayList<Song> songs) {
        Connection connection = null;
        Statement statement;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:music.db");
            statement = connection.createStatement();
            statement.executeUpdate("drop table if exists albums;");
            statement.executeUpdate("drop table if exists artists;");
            statement.executeUpdate("drop table if exists songs;");
            statement.executeUpdate("CREATE TABLE albums (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL, artist INTEGER NOT NULL);");
            statement.executeUpdate("create table artists (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL);");
            statement.executeUpdate("create table songs (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL, artist INTEGER NOT NULL, album INTEGER NOT NULL, genre TEXT);");
            for (Song song : songs) {
                String sql_command_1 = song.toSQL();
                statement.executeUpdate(sql_command_1);
            }
            String sql_command_2 = songs.get(0).artist.toSQL();
            statement.executeUpdate(sql_command_2);
            String sql_command_3 = songs.get(0).album.toSQL();
            statement.executeUpdate(sql_command_3);
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }
    public static void songToSQL(Song song) {
        Connection connection = null;
        Statement statement;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:music.db");
            statement = connection.createStatement();
            statement.executeUpdate("drop table if exists songs;");
            statement.executeUpdate("drop table if exists albums;");
            statement.executeUpdate("drop table if exists artists;");
            statement.executeUpdate("CREATE TABLE albums (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL, artist INTEGER NOT NULL);");
            statement.executeUpdate("create table artists (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL);");
            statement.executeUpdate("CREATE TABLE songs (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL, artist INTEGER NOT NULL, album INTEGER NOT NULL, genre TEXT);");
            String sql_command_1 = song.toSQL();
            statement.executeUpdate(sql_command_1);
            String sql_command_2 = song.artist.toSQL();
            statement.executeUpdate(sql_command_2);
            String sql_command_3 = song.album.toSQL();
            statement.executeUpdate(sql_command_3);
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    public static void mainMenu(){
        while (true) {
            mainInterface();
            Scanner sc = new Scanner(System.in);
            String usersChoice = sc.next().trim();
            switch (usersChoice) {
                case "1" -> searchInterface(sc);
                case "2" -> recommendInterface(sc);
                case "3" -> playlistInterface(sc);
                case "4" -> {
                    System.out.println("Hope to see you next time. Bye!");
                    sc.close();
                    System.exit(0);
                }
                default -> System.out.println("Invalid Input. Please try to enter a number from 1 to 4.");
            }
        }
    }

    public static void mainInterface() {
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("Hello!");
        System.out.println("I am your music manager.");
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("Main Menu:");
        System.out.println(" 1 -- Search & Display");
        System.out.println(" 2 -- Recommend");
        System.out.println(" 3 -- Playlist");
        System.out.println(" 4 -- Exit");
        System.out.println("Please enter your choice, eg. '1'.");
    }

    public static void searchInterface(Scanner sc) {
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("What would you like to search?");
        System.out.println(" 1 -- Artist Name (will display all albums)");
        System.out.println(" 2 -- Artist Name + Album Name (will display all songs under this album)");
        System.out.println(" 3 -- Artist Name + Song Name (will display this song's info)");
        System.out.println(" 4 -- Back to main menu");
        System.out.println("Please enter your choice, eg. '1'.");
        while (true) {
            String usersChoice = sc.next().trim();
            switch (usersChoice) {
                case "1" -> searchInterface_1(sc);
                case "2" -> searchInterface_2(sc);
                case "3" -> searchInterface_3(sc);
                case "4" -> mainMenu();
                default -> System.out.println("Invalid Input, please try again.");
            }
        }
    }

    public static void searchInterface_1(Scanner sc) {
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("What is the artist name? (please connect words with one single underscore \"_\")");
        System.out.println("e.g. please enter Taylor_Swift instead of Taylor Swift.");
        String usersChoice_1 = sc.next().trim();
        importArtist(usersChoice_1);
        albumsToSQL(albums);
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("Here are all albums: ");
        System.out.println("-----------------------------------------------------------------------------------");
        for (Album album: albums) {
            System.out.println(album.album_name);
        }
        System.out.println("-----------------------------------------------------------------------------------");
        searchInterface(sc);
    }

    public static void searchInterface_2(Scanner sc) {
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("What is the artist name? (please connect words with one single underscore \"_\")");
        System.out.println("e.g. please enter Taylor_Swift instead of Taylor Swift.");
        String usersChoice_2_1 = sc.next().trim();
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("What is the album name? (please connect words with one single underscore \\\"_\\\")\"");
        System.out.println("e.g. please enter Let_It_Be instead of Let It Be.");
        String usersChoice_2_2 = sc.next().trim();
        importAlbum(usersChoice_2_1, usersChoice_2_2);
        songsToSQL(songs);
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("Here are all songs under this album: ");
        System.out.println("-----------------------------------------------------------------------------------");
        for (Song song : songs) {
            System.out.println(song.song_name);
        }
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("Do you want to add them to your playlist? (Y/N)");
        String usersChoice_2_3 = sc.next().trim();
        if (usersChoice_2_3.equals("Y") || usersChoice_2_3.equals("y")) {
            p.songlist = songs;
            searchInterface(sc);
        } else {
            searchInterface(sc);
        }
    }

    public static void searchInterface_3(Scanner sc) {
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("What is the artist name? (please connect words with one single underscore \"_\")");
        System.out.println("e.g. please enter Taylor_Swift instead of Taylor Swift.");
        String usersChoice_3_1 = sc.next().trim();
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("What is the song name? (please connect words with one single underscore \\\"_\\\")\"");
        System.out.println("e.g. Please enter Come_Together instead of Come Together.");
        String usersChoice_3_2 = sc.next().trim();
        importSong(usersChoice_3_1, usersChoice_3_2);
        songToSQL(song);
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("Here are some information of this song: ");
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("Song Name: " + song.song_name);
        System.out.println("Artist Name: " + song.artist.artist_name);
        System.out.println("Album Name: " + song.album.album_name);
        System.out.println("Song Genre: " + song.song_genre);
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("Do you want to add this song to your playlist? (Y/N)");
        String usersChoice_3_3 = sc.next().trim();
        if (usersChoice_3_3.equals("Y") || usersChoice_3_3.equals("y")) {
            p.songlist.add(song);
            searchInterface(sc);
        } else {
            searchInterface(sc);
        }
        searchInterface(sc);
    }

    public static void recommendInterface(Scanner sc) {
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("What would you like me to recommend?");
        System.out.println(" 1 -- Songs");
        System.out.println(" 2 -- Albums");
        System.out.println(" 3 -- Artist");
        System.out.println(" 4 -- Back to main menu");
        System.out.println("Please enter your choice, eg. '1'.");
        while (true) {
            String usersChoice = sc.next().trim();
            switch (usersChoice) {
                case "1" -> recommendSong(sc);
                case "2" -> recommendAlbum(sc);
                case "3" -> recommendArtist(sc);
                case "4" -> mainMenu();
                default -> System.out.println("Invalid Input, please try again.");
            }
        }
    }

    public static void recommendSong(Scanner sc) {
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("Here are top 10 popular songs: ");
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("White Christmas");
        System.out.println("Shape of You");
        System.out.println("Despacito");
        System.out.println("Candle in the Wind");
        System.out.println("In the Summertime");
        System.out.println("Silent Night");
        System.out.println("Rock Around the Clock");
        System.out.println("I Will Always Love You");
        System.out.println("Something Just Like This");
        System.out.println("Perfect");
        System.out.println("-----------------------------------------------------------------------------------");
        recommendInterface(sc);
    }

    public static void recommendAlbum(Scanner sc) {
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("Here are top 10 best-selling albums: ");
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("Thriller");
        System.out.println("Their Greatest Hits");
        System.out.println("Led Zeppelin IV");
        System.out.println("Back in Black");
        System.out.println("Come On Over");
        System.out.println("Rumours");
        System.out.println("The Bodyguard");
        System.out.println("21");
        System.out.println("The Dark Side of the Moon");
        System.out.println("Jagged Little Pill");
        System.out.println("-----------------------------------------------------------------------------------");
        recommendInterface(sc);
    }

    public static void recommendArtist(Scanner sc) {
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("Here are top 10 popular artists: ");
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("Ed Sheeran");
        System.out.println("Ariana Grande");
        System.out.println("Billie Eilish");
        System.out.println("The Weeknd");
        System.out.println("Justin Bieber");
        System.out.println("Taylor Swift");
        System.out.println("Shawn Mendes");
        System.out.println("Dua Lipa");
        System.out.println("Charlie Puth");
        System.out.println("Eminem");
        System.out.println("-----------------------------------------------------------------------------------");
        recommendInterface(sc);
    }

    public static void playlistInterface(Scanner sc) {
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("What would you like to do with playlist?");
        System.out.println(" 1 -- View my playlist (please go to search albums/songs to add songs to playlist first.");
        System.out.println(" 2 -- Generate an XML file containing my playlist");
        System.out.println(" 3 -- Back to main menu");
        System.out.println("Please enter your choice, eg. '1'.");
        while (true) {
            String usersChoice = sc.next().trim();
            switch (usersChoice) {
                case "1" -> viewPlaylist(sc);
                case "2" -> playlistToXML(sc);
                case "3" -> mainMenu();
                default -> System.out.println("Invalid Input, please try again.");
            }
        }
    }

    public static void viewPlaylist(Scanner sc) {
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("Here are all songs in your playlist: ");
        System.out.println("-----------------------------------------------------------------------------------");
        for (Song song: p.songlist) {
            System.out.println(song.song_name);
        }
        System.out.println("-----------------------------------------------------------------------------------");
        playlistInterface(sc);
    }

    public static void playlistToXML(Scanner sc) {
        String library = "<?xml version=\"1.0\"  ?>\n" +
                "<playlist>\n";
        String songList = "<songs>\n";
        String result = library + songList;
        for (Song song: p.songlist) {
            result += song.songToXML();
        }
        result = result + "</songs>\n" + "</playlist>\n";
        FileWriter xmlFile = null;
        try {
            xmlFile = new FileWriter("Playlist.xml");
            xmlFile.write(result);
            xmlFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("After you go back to the main menu and exit the system, there will be an xml file named \"Playlist\".");
        playlistInterface(sc);
    }

    public static void main(String[] args) {
        Library l = new Library();
        mainMenu();
    }
}
