package com.songs.inforet;

import java.io.FileReader;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService{
    private StandardAnalyzer analyzer;
    private IndexWriterConfig config;
    private IndexWriter w;
    private Directory index;
    private final String INDEX_PATH = "/tmp/project/inforetr";
    private IndexSearcher searcher;
    private final int NUM_OF_HITS = 60;

    private final int NUM_OF_HITS_EMBED = 10;

    private final int MAX_NUM_OF_SEARCHES = 20;
    private final int NONE = 0;
    private final int AND = 1;
    private final int OR = 2;
    private final int NOT = 3;

    private static String suggestion = "";

    private static final String filePath = "your_path_to_the_pretrained_model.txt";

    @Autowired
    private SearchDAO searchRepository;

    @Override
    @Transactional
    public List<Song> searchSongs(String query, String field, int booleanFlag, boolean sortByTitle, boolean incognitoFlag, boolean mlFlag) {
        try {
            analyzer = new StandardAnalyzer();
            Directory directory = FSDirectory.open(Paths.get(INDEX_PATH));
            DirectoryReader reader= DirectoryReader.open(directory);

            List<Song> songs;
            suggestion = "";
            if(!mlFlag){
                //retrieve songs from index
                songs = retrieveSongs(query, field, booleanFlag, reader, false);
            }
            else{
                //get relevantWords and print them
                String[] relevantWords = this.getRelevantWords(query);
                songs = new ArrayList<>();
                for (String relevantWord : relevantWords) {
                    System.out.println("-> " + relevantWord);
                    List<Song> currentSongs = retrieveSongs(relevantWord, field, booleanFlag, reader, true);
                    if(currentSongs.size()>0) System.out.println(currentSongs.get(0).getTitle());
                    for(Song song : currentSongs) {
                        songs.add(song);
                    }
                    if(suggestion.equals("")){suggestion = relevantWord;}
                    else{suggestion = suggestion + ", " + relevantWord;}

                }
                System.out.println("Suggestion is: "+suggestion);
            }
            reader.close();
            //return the songs after do some checks
            return this.getFinalSongs(songs, query, sortByTitle, incognitoFlag, suggestion);
        }
        catch(IOException io) {
            io.printStackTrace();
        }
        /*should never be executed*/return null;
    }

    @Override
    @Transactional
    public void initializeIndex(String fileName){
        try {
            analyzer = new StandardAnalyzer();
            index = FSDirectory.open(Paths.get(INDEX_PATH));
            config = new IndexWriterConfig(analyzer);
            w = new IndexWriter(index, config);

            FileReader fr = new FileReader(fileName);
            CSVParser csvParser = new CSVParser(fr, CSVFormat.DEFAULT);

            for (CSVRecord csvRecord : csvParser) { //iterate line by line
                addDoc(w, csvRecord.get(0), csvRecord.get(1), csvRecord.get(2));
            }
            System.out.println("Number of docs are: "+w.numRamDocs());
            csvParser.close();
            w.close();
        }
        catch(IOException io) {
            io.printStackTrace();
        }
    }

    @Override
    @Transactional
    public List<Search> getSearchHistory(){
        return searchRepository.findAll();
    }


    public void addSearch(String query, String topSongTitle, String suggestion) {
        List<Search> allSearches = searchRepository.findAll();
        int currentMaxSlot = allSearches.size();
        if(currentMaxSlot < MAX_NUM_OF_SEARCHES) {
            searchRepository.save(new Search(currentMaxSlot+1, query, topSongTitle, suggestion));
        }else {
            List<Search> updatedSearches = new ArrayList<Search>();
            int slot;
            String sentence, currentTopSongTitle, currentSuggestion;
            for(int i=1; i<allSearches.size(); i++) {
                slot = i;
                sentence = allSearches.get(i).getSentence();
                currentTopSongTitle = allSearches.get(i).getTopSong();
                currentSuggestion = allSearches.get(i).getSuggestion();
                updatedSearches.add(new Search(slot, sentence, currentTopSongTitle, currentSuggestion));
            }updatedSearches.add(new Search(MAX_NUM_OF_SEARCHES, query, topSongTitle, suggestion));

            for(Search search : updatedSearches) {
                searchRepository.save(search);
            }
        }
    }

    private Query getFinalQuery(String query, String field, int booleanFlag){
        try {
            Query q;
            if (booleanFlag==NONE) {
                q = new QueryParser(field, analyzer).parse(query);
            } else {
                String[] words = query.split("@@"); // split query into individual words
                BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
                for (String word : words) {
                    if(booleanFlag==AND) {
                        Query subQuery = new QueryParser(field, analyzer).parse(word);
                        queryBuilder.add(subQuery, BooleanClause.Occur.MUST);
                    }
                    else if(booleanFlag==OR) {
                        Query subQuery = new QueryParser(field, analyzer).parse(word);
                        queryBuilder.add(subQuery, BooleanClause.Occur.SHOULD);
                    }
                    else if(booleanFlag==NOT) {
                        Query subQuery = new QueryParser(field, analyzer).parse(word);
                        queryBuilder.add(subQuery, BooleanClause.Occur.MUST_NOT);
                    }
                }
                q = queryBuilder.build();
            }return q;
        }catch(ParseException e){
            e.printStackTrace();
            return null;
        }
    }

    private List<Song> retrieveSongs(String query, String field, int booleanFlag, DirectoryReader reader, boolean mlFlag) throws IOException {
        int numberOfHits;
        if(!mlFlag){
            numberOfHits = NUM_OF_HITS;
        }else{
            numberOfHits = NUM_OF_HITS_EMBED;
        }
        Query q = this.getFinalQuery(query, field, booleanFlag);
        searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, numberOfHits);
        ScoreDoc[] hits = docs.scoreDocs;
        List<Song> songs = new ArrayList<Song>();
        for (ScoreDoc hit : hits) {
            int docId = hit.doc;
            Document d = searcher.doc(docId);
            songs.add(new Song(d.get("artist"), d.get("title"), d.get("lyrics")));
        }
        return songs;
    }

    private List<Song> getFinalSongs(List<Song> songs, String query, boolean sortByTitle, boolean incognitoFlag, String suggestion){
        if(songs.size()==0) {
            return songs;
        }
        if(!incognitoFlag) {
            this.addSearch(query, songs.get(0).getTitle(), suggestion);
        }
        if(sortByTitle) {
            return this.sortSongsByTitle(songs);
        }else {
            return songs;
        }
    }

    private String[] getRelevantWords(String query) throws IOException{
        String parsedQuery = this.getParsedQuery(query);
        Word2VecSimilarity mlObject = new Word2VecSimilarity(filePath);
        return mlObject.getSimilarWordsFromPhrase(parsedQuery, 10);
    }

    private String getParsedQuery(String query) {
        return query.replaceAll("@@", " ");
    }


    private static void addDoc(IndexWriter w, String artist, String title, String lyrics) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("artist", artist, Field.Store.YES));
        doc.add(new TextField("lyrics", lyrics, Field.Store.YES));
        w.addDocument(doc);
    }

    private List<Song> sortSongsByTitle(List<Song> songs) {
        // Create a copy of the input list to avoid modifying the original list
        List<Song> sortedSongs = new ArrayList<>(songs);

        // Sort the songs by the number of words in their titles, from least to most
        Collections.sort(sortedSongs, new Comparator<Song>() {
            public int compare(Song s1, Song s2) {
                int numWordsS1 = s1.getTitle().split("\\s+").length;
                int numWordsS2 = s2.getTitle().split("\\s+").length;
                return Integer.compare(numWordsS1, numWordsS2);
            }
        });

        return sortedSongs;
    }


    //EXECUTE THIS MAIN FUNCTION WITH OUT THE COMMENTS TO CREATE THE INDEX.
    //this is the first thing you should do before
    //after that you can run the application with out having to initialize the index again.
    public static void main(String args[]) {
        //SearchServiceImpl service = new SearchServiceImpl();
        //service.initializeIndex("your_path_to_songs.csv"); //GIVE THE PATH for recourses/DDL/songs.csv
        //service.searchSongs("love girl", "title", 1);
        System.out.println("Done");
    }

}
