package model;

import java.awt.image.BufferedImage;

public class Song {
    private final String title;
    private final String artist;
    private final String folderName;
    private final String genre;
    private final String duration;
    private final BufferedImage thumbnail;

    public Song(String title, String artist, String folderName, String genre, String duration, BufferedImage thumbnail) {
        this.title      = title;
        this.artist     = artist;
        this.folderName = folderName;
        this.genre      = genre;
        this.duration   = duration;
        this.thumbnail  = thumbnail;
    }

    public String getTitle()           { return title; }
    public String getArtist()          { return artist; }
    public String getFolderName()      { return folderName; }
    public String getGenre()           { return genre; }
    public String getDuration()        { return duration; }
    public BufferedImage getThumbnail(){ return thumbnail; }

    @Override
    public String toString() { return title + " - " + artist; }
}
