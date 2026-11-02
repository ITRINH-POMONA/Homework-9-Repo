package org.example;

public class Song {
    private final Integer songID;
    private final String title;
    private final String artist;
    private final String album;
    private final int duration;   // seconds

    public Song(Integer songID, String title, String artist, String album, int duration) {
        this.songID = songID;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }

    public Integer getSongID() { return songID; }
    public String getTitle()   { return title; }
    public String getArtist()  { return artist; }
    public String getAlbum()   { return album; }
    public int getDuration()   { return duration; }

    @Override
    public String toString() {
        return "Song{id=" + songID + ", title='" + title + "', artist='" + artist
                + "', album='" + album + "', duration=" + duration + "s}";
    }
}
