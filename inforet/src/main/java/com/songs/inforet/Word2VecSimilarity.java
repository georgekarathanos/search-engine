package com.songs.inforet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;


public class Word2VecSimilarity {

    private Map<String, INDArray> wordVectors = new HashMap<>();

    private static final int shape_num = 50;
    private static final String filePath = "C:\\Users\\giorg\\Documents\\cse uoi\\8th semester\\Information Retrieval\\Project\\model\\filtered_model.txt";

    public Word2VecSimilarity(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            String word = parts[0];
            float[] vector = new float[parts.length - 1];
            for (int i = 1; i < parts.length; i++) {
                vector[i - 1] = Float.parseFloat(parts[i]);
            }
            INDArray wordVector = Nd4j.create(vector);
            wordVectors.put(word, wordVector);
        }
        reader.close();
    }

    /*
    public String[] getSimilarWords(String word, int n) {
        INDArray queryVector = wordVectors.get(word);
        if (queryVector == null) {
            return new String[0];
        }
        Map<String, Double> distances = new HashMap<>();
        for (Map.Entry<String, INDArray> entry : wordVectors.entrySet()) {
            String candidateWord = entry.getKey();
            INDArray candidateVector = entry.getValue();
            double distance = Transforms.cosineSim(queryVector, candidateVector);
            distances.put(candidateWord, distance);
        }
        return distances.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).limit(n).map(Map.Entry::getKey).toArray(String[]::new);
    }*/

    public String[] getSimilarWordsFromPhrase(String input, int n) {
        String[] inputWords = input.toLowerCase().split("\\s+"); // split input into individual words and convert to lowercase
        INDArray queryVector = Nd4j.zeros(shape_num); // initialize a zero vector of the same dimensionality as the pre-trained vectors
        int count = 0;
        for (String word : inputWords) {
            INDArray wordVector = wordVectors.get(word);
            if (wordVector != null) { // only add the vector if the word is in the pre-trained embeddings
                queryVector.addi(wordVector);
                count++;
            }
        }
        if (count == 0) {
            return new String[0];
        }
        queryVector.divi(count); // take the average of the constituent vectors
        Map<String, Double> distances = new HashMap<>();
        for (Map.Entry<String, INDArray> entry : wordVectors.entrySet()) {
            String candidateWord = entry.getKey();
            INDArray candidateVector = entry.getValue();
            double distance = Transforms.cosineSim(queryVector, candidateVector);
            distances.put(candidateWord, distance);
        }
        return distances.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).limit(n+inputWords.length).map(Map.Entry::getKey).toArray(String[]::new);
    }

    public static void main(String[] args) throws IOException {
        Word2VecSimilarity similarity = new Word2VecSimilarity(filePath);
        String queryWord = "Black mirror";
        String[] similarWords = similarity.getSimilarWordsFromPhrase(queryWord, 10);
        for (String word : similarWords) {
            System.out.println(word);
        }System.out.println("Done");
    }
}