package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import browser.NgordnetQueryType;
import ngrams.NGramMap;
import ngrams.TimeSeries;

import java.util.*;

public class HyponymsHandler extends NgordnetQueryHandler {
    WordNet wn;
    NGramMap ngm;

    public HyponymsHandler(WordNet wn, NGramMap ngm) {
        this.wn = wn;
        this.ngm = ngm;
    }

    //Returns a list of hyponyms, formatted as a String, for the word(s) inputted into the query
    public String handle(NgordnetQuery q) {
        List<String> words = q.words();
        int startYear = q.startYear();
        int endYear = q.endYear();
        int k = q.k();
        NgordnetQueryType queryType = q.ngordnetQueryType();

        if (queryType.equals(NgordnetQueryType.ANCESTORS)) {
            Set<String> ancestorsOfWords = wn.getHypernymsOf(words);

            //if k = 0, return all hypernyms of given words
            if (k == 0) {
                return "[" + String.join(", ", ancestorsOfWords) + "]";
            } else {
                //else, return the k most popular hypernyms of the given words
                Set<String> mostPopularAncestors = findKMostPopularWords(ancestorsOfWords, startYear, endYear, k);
                return "[" + String.join(", ", mostPopularAncestors) + "]";
            }

        } else if (queryType.equals(NgordnetQueryType.HYPONYMS)) {
            Set<String> hyponymsOfWords = wn.getHyponymsOf(words);

            //if k = 0, return all hyponyms of given words
            if (k == 0) {
                return "[" + String.join(", ", hyponymsOfWords) + "]";
            } else {
                //else, return the most popular hyponyms of words in given time period
                Set<String> mostPopularHyponyms = findKMostPopularWords(hyponymsOfWords, startYear, endYear, k);
                return "[" + String.join(", ", mostPopularHyponyms) + "]";
            }
        }
        throw new NoSuchElementException("invalid query type");
    }


    //returns the set of k words with the largest frequencies in the given interval
    //type of words depends on user query (ancestors or hyponyms)
    public Set<String> findKMostPopularWords(Set<String> queryWords, int startYear, int endYear, int k) {
        //map of summed frequencies over time period to associated words
        TreeMap<Double, Set<String>> wordCounts = new TreeMap<>();
        Set<String> returnWords = new TreeSet<>();

        //arrange the hyponyms in decreasing order of total frequency of appearances in given time frame
        for (String eachWord : queryWords) {
            //countHistory() returns a TS that maps each year a word appears to its count of appearances in each year
            TimeSeries wordCountHistory = ngm.countHistory(eachWord, startYear, endYear);

            //if a word does not appear in the NGramMap, we will not include that word in the return Set
            //countHistory() returns an empty TS if the word does not appear in the ngrams words data file
            if (wordCountHistory.isEmpty()) {
                continue;
            }

            //sum all counts of this word's appearances across the given years
            double sum = 0.0;
            for (int eachYear : wordCountHistory.keySet()) {
                sum += wordCountHistory.get(eachYear);
            }
            //countHistory() only returns an empty TS if the word does not appear in the ngrams words data file
            //however, it might appear, but just not within the specified time frame, so we must make sure to not
            //include the word in the return Set if it has zero counts throughout the specified time frame
            if (sum == 0.0) {
                continue;
            }

            //if this word's total frequency is already a key in the Map, add this word to that frequency values' list
            if (wordCounts.containsKey(sum)) {
                wordCounts.get(sum).add(eachWord);
            } else {
                //insert this summed frequency-wordSet pair into the wordCounts Map
                Set<String> wordsWithThisFrequency = new HashSet<>();
                wordsWithThisFrequency.add(eachWord);
                wordCounts.put(sum, wordsWithThisFrequency);
            }
        }

        //add the top k words with the greatest frequency values over the given time period to the return Set
        for (Double nextFrequency : wordCounts.descendingKeySet()) {
            //For each summed frequency, access the corresponding Set of words and add each word to return Set
            for (String eachWord : wordCounts.get(nextFrequency)) {
                if (returnWords.size() == k) {
                    return returnWords;
                }
                returnWords.add(eachWord);
            }
        }
        return returnWords;
    }
}
