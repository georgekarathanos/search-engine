package com.songs.inforet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="search")
public class Search {

    @Id
    @Column(name="slot")
    private int slot;

    @Column(name="sentence")
    private String sentence;

    @Column(name="top_song")
    private String topSong;

    @Column(name="suggestion")
    private String suggestion;

    public Search(int slot, String sentence, String topSong, String suggestion) {
        this.slot = slot;
        this.sentence = sentence;
        this.topSong = topSong;
        this.suggestion = suggestion;
    }

    public Search() {}

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getTopSong() {
        return topSong;
    }

    public void setTopSong(String topSong) {
        this.topSong = topSong;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    @Override
    public String toString() {
        return "Search{" +
                "slot=" + slot +
                ", sentence='" + sentence + '\'' +
                ", topSong='" + topSong + '\'' +
                ", suggestion='" + suggestion + '\'' +
                '}';
    }
}
