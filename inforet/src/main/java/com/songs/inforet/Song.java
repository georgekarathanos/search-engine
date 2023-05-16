package com.songs.inforet;

public class Song {

    private String artist;
    private String title;
    private String lyrics;

    public Song() {
        super();
    }

    public Song(String artist, String title, String lyrics) {
        super();
        this.artist = artist;
        this.title = title;
        this.lyrics = lyrics;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    @Override
    public String toString() {
        return "Song [artist=" + artist + ", title=" + title + ", lyrics=" + lyrics + "]";
    }
}

