package model;

import java.awt.image.BufferedImage;

public record Song(String title, String artist, String folderName, String genre, String duration,
                   BufferedImage thumbnail) {

    @Override
    public String toString() {
        return title + " - " + artist;
    }
}
