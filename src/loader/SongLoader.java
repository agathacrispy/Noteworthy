package loader;

import model.LyricLine;
import model.PitchFrame;
import model.Song;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SongLoader {
    public ArrayList<LyricLine> lyrics = new ArrayList<LyricLine>();
    public ArrayList<PitchFrame> pitches = new ArrayList<PitchFrame>();

    public ArrayList<Song> loadSongs() {
        ArrayList<Song> songs = new ArrayList<>();
        Path path = Paths.get("songs");
        try {
            ArrayList<Path> songPaths = Files.list(path)
                    .filter(Files::isDirectory)
                    .collect(Collectors.toCollection(ArrayList::new));
            for (Path p : songPaths) {
                String folderName = p.getFileName().toString();
                String title = folderName;
                String artist = "";

                String genre = "", duration = "";
                Properties props = new Properties();
                try (InputStream is = new FileInputStream(p.resolve("info.properties").toFile())) {
                    props.load(is);
                    title = props.getProperty("title", folderName);
                    artist = props.getProperty("artist", "");
                    genre = props.getProperty("genre", "");
                    duration = props.getProperty("duration", "");
                } catch (IOException ignored) {
                }

                BufferedImage thumbnail = null;
                try {
                    thumbnail = ImageIO.read(p.resolve("thumbnail.jpg").toFile());
                } catch (IOException ignored) {
                }

                songs.add(new Song(title, artist, folderName, genre, duration, thumbnail));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return songs;
    }

    public void loadLyrics(String song) {
        try {
            FileReader fr = new FileReader("songs/" + song + "/lyrics.lrc");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                Pattern pattern = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2})\\](.*)");
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    long minutes = Long.parseLong(matcher.group(1));
                    long seconds = Long.parseLong(matcher.group(2));
                    long centiseconds = Long.parseLong(matcher.group(3));
                    String text = matcher.group(4).trim();
                    long startMs = minutes * 60000 + seconds * 1000 + centiseconds * 10;
                    if (lyrics.getNextLine() != null) {
                        lyrics.add(new LyricLine(text, startMs));
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("can't read");
        }
    }

    public void loadPitches(String song) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("songs/" + song + "/pitches.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                long ms = Long.parseLong(parts[0].trim());
                int midi = Integer.parseInt(parts[1].trim());
                pitches.add(new PitchFrame(ms, midi));
            }
            br.close();
        } catch (IOException e) {
            System.out.println("can't read pitches");
        }
    }

}
