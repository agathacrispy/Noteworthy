package loader;

import model.LyricLine;
import model.PitchFrame;
import model.Song;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;

public class SongLoader {
    public ArrayList<LyricLine> lyrics = new ArrayList<LyricLine>();
    public ArrayList<PitchFrame> pitches = new ArrayList<PitchFrame>();

    public void loadLyrics() {
        try
        {
            FileReader fr = new FileReader("songs/dangerous-woman/lyrics.lrc");
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

    public void loadPitches() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("songs/dangerous-woman/pitches.csv"));
            String line;
            br.readLine(); // skip header
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
