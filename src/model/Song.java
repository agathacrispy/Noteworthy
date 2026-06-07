package model;

import java.util.List;

public class Song {
    private final String title;
    private final String artist;
    private final String folderName;

    public Song(String title, String artist, String folderName) {
        this.title = title;
        this.artist = artist;
        this.folderName = folderName;
    }

    public String getTitle()      { return title; }
    public String getArtist()     { return artist; }
    public String getFolderName() { return folderName; }

    @Override
    public String toString() { return title + " - " + artist; }
}
