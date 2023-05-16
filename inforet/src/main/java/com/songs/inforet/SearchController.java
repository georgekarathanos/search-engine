package com.songs.inforet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    @Autowired
    public SearchService service;

    @GetMapping("/")
    public String home() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String first(Model theModel) {
        List<Search> searches = service.getSearchHistory();
        int num_of_searches = searches.size();
        List<Search> history = new ArrayList<Search>();
        for(int i = num_of_searches-1; i>=0; i--) {
            int slot = num_of_searches - searches.get(i).getSlot() + 1;
            String sentence = searches.get(i).getSentence();
            String topSong = searches.get(i).getTopSong();
            String suggestion = searches.get(i).getSuggestion();
            history.add(new Search(slot, sentence, topSong, suggestion));
        }
        theModel.addAttribute("searches", history);
        return "search";
    }

    @GetMapping("/search")
    public String searchSongs(@RequestParam("query") String query, @RequestParam("field") String field,
                              @RequestParam("booleanFlag") int booleanFlag,@RequestParam("sortFlag") boolean sortFlag,
                              @RequestParam("incognitoFlag") boolean incognitoFlag,@RequestParam("mlFlag") boolean mlFlag, Model theModel) {
        if(!query.equals("")) {
            long startTime = System.nanoTime();
            List<Song> songs = service.searchSongs(query, field, booleanFlag, sortFlag, incognitoFlag, mlFlag);
            long endTime = System.nanoTime();
            long elapsedTime = (endTime - startTime) / 1000000000; // Convert nanoseconds to seconds
            System.out.println("Elapsed time: " + elapsedTime + " seconds");
            theModel.addAttribute("songs", songs);
            theModel.addAttribute("query", query);
            return "songs";
        }
        return "redirect:/index";
    }

    @GetMapping("/viewSong")
    public String viewSong(@RequestParam("artist") String artist, @RequestParam("title") String title, @RequestParam("lyrics") String lyrics,
                           @RequestParam("query") String query, Model theModel) {
        Song theSong = new Song(artist, title, lyrics);
        theModel.addAttribute("song", theSong);
        List<String> queryList = Arrays.asList(query.split("@@"));
        theModel.addAttribute("queryList", queryList);
        return "song-view";
    }

    @GetMapping("/incognito")
    public String goIncognito() {
        return "incognito-search";
    }
}
