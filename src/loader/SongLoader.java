package loader;

import model.LyricLine;
import model.PitchFrame;

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

    public ArrayList<String> loadSongs() {
        ArrayList<String> songs = new ArrayList<>();
        Path path = Paths.get("songs");
        try {
            ArrayList<Path> songPaths = Files.list(path)
                    .filter(Files::isDirectory)
                    .collect(Collectors.toCollection(ArrayList::new));
            for (Path p : songPaths) {
                songs.add(p.getFileName().toString());
                System.out.println(p.getFileName().toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return songs;
    }

    public void loadLyrics(String song) {
        try
        {
            FileReader fr = new FileReader("songs/" + song + "/lyrics.lrc");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null)
            {
                Pattern pattern = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2})\\](.*)");
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    long minutes = Long.parseLong(matcher.group(1));
                    long seconds = Long.parseLong(matcher.group(2));
                    long centiseconds = Long.parseLong(matcher.group(3));
                    String text = matcher.group(4).trim();
                    //System.out.println(text);
                    long startMs = minutes * 60000 + seconds * 1000 + centiseconds * 10;
                    if (lyrics != null) { lyrics.add(new LyricLine(text, startMs)); }
                }
            }
            br.close();
        }
        catch(IOException e)
        {
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
