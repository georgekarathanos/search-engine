package com.songs.inforet;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface SearchService {

    public List<Song> searchSongs(String query, String field, int booleanFlag, boolean sortByTitle, boolean incognitoFlag, boolean mlFlag);

    public void initializeIndex(String fileName);

    public List<Search> getSearchHistory();

}
