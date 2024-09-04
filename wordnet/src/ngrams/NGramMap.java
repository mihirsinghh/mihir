package ngrams;

import edu.princeton.cs.algs4.In;

import java.util.Collection;
import java.util.TreeMap;

/**
 * An object that provides utility methods for making queries on the
 * Google NGrams dataset (or a subset thereof).
 *
 * An NGramMap stores pertinent data from a "words file" and a "counts
 * file". It is not a map in the strict sense, but it does provide additional
 * functionality.
 *
 * @author Josh Hug
 */
public class NGramMap {
    TreeMap<String, TimeSeries> wordFrequencyMap;
    TreeMap<Integer, Long> yearlyWordCountMap;

    /**
     * Constructs an NGramMap from WORDSFILENAME and COUNTSFILENAME.
     */
    public NGramMap(String wordsFilename, String countsFilename) {
        wordFrequencyMap = new TreeMap<>();
        yearlyWordCountMap = new TreeMap<>();

        In wordsFile = new In(wordsFilename);
        In countsFile = new In(countsFilename);

        while (!wordsFile.isEmpty()) {
            String nextWordLine = wordsFile.readLine(); //get the next line
            String[] splitWordLine = nextWordLine.split("\t"); //array containing each "word" on the line

            //if a word already exists as a key, update its corresponding time series with another year-frequency pair
            if (wordFrequencyMap.containsKey(splitWordLine[0])) {
                wordFrequencyMap.get(splitWordLine[0]).put(Integer.parseInt(splitWordLine[1]),
                        Double.parseDouble(splitWordLine[2]));

            } else { //else, create a new word-time series pair in the map
                TimeSeries wordTimeSeries = new TimeSeries();
                wordTimeSeries.put(Integer.parseInt(splitWordLine[1]), Double.parseDouble(splitWordLine[2]));
                wordFrequencyMap.put(splitWordLine[0], wordTimeSeries);
            }
        }

        while (!countsFile.isEmpty()) {
            String nextCountLine = countsFile.readLine();
            String[] splitCountLine = nextCountLine.split(",");

            //each year appears only once in the file, so create a new year-word count pair in the map
            yearlyWordCountMap.put(Integer.parseInt(splitCountLine[0]), Long.parseLong(splitCountLine[1]));
        }
    }

    /**
     * Provides the history of WORD between STARTYEAR and ENDYEAR, inclusive of both ends. The
     * returned TimeSeries should be a copy, not a link to this NGramMap's TimeSeries. In other
     * words, changes made to the object returned by this function should not also affect the
     * NGramMap. This is also known as a "defensive copy". If the word is not in the data files,
     * returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word, int startYear, int endYear) {
        if (!wordFrequencyMap.containsKey(word)) {
            return new TimeSeries();
        } else {
            TimeSeries wordTSCopy = new TimeSeries();
            TimeSeries wordOriginalTS = wordFrequencyMap.get(word);

            for (int i = startYear; i <= endYear; i++) {
                if (wordOriginalTS.containsKey(i)) {
                    wordTSCopy.put(i, wordOriginalTS.get(i));
                }
            }
            return wordTSCopy;
        }
    }

    /**
     * Provides the history of WORD. The returned TimeSeries should be a copy, not a link to this
     * NGramMap's TimeSeries. In other words, changes made to the object returned by this function
     * should not also affect the NGramMap. This is also known as a "defensive copy". If the word
     * is not in the data files, returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word) {
        if (!wordFrequencyMap.containsKey(word)) {
            return new TimeSeries();
        } else {
            TimeSeries wordTSCopy = new TimeSeries();
            TimeSeries originalWordTS = wordFrequencyMap.get(word);

            for (int eachYear : originalWordTS.keySet()) {
                wordTSCopy.put(eachYear, originalWordTS.get(eachYear));
            }
            return wordTSCopy;
        }
    }

    /**
     * Returns a defensive copy of the total number of words recorded per year in all volumes.
     */
    public TimeSeries totalCountHistory() {
        TimeSeries totalWordsPerYear = new TimeSeries();
        for (int eachYear : yearlyWordCountMap.keySet()) {
            totalWordsPerYear.put(eachYear, Double.valueOf(yearlyWordCountMap.get(eachYear)));
        }
        return totalWordsPerYear;
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD between STARTYEAR
     * and ENDYEAR, inclusive of both ends. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word, int startYear, int endYear) {
        if (!wordFrequencyMap.containsKey(word)) {
            return new TimeSeries();
        } else {
            TimeSeries wordRelativeFreqTS = new TimeSeries();
            TimeSeries originalWordTS = wordFrequencyMap.get(word);

            for (int i = startYear; i <= endYear; i++) {
                if (originalWordTS.containsKey(i)) {
                    double thisWordCountThisYear = originalWordTS.get(i);

                    if (yearlyWordCountMap.containsKey(i) && yearlyWordCountMap.get(i) != 0) {
                        long totalYearlyCount = yearlyWordCountMap.get(i);
                        wordRelativeFreqTS.put(i, thisWordCountThisYear / totalYearlyCount);
                    }
                }
            }
            return wordRelativeFreqTS;
        }
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD compared to all
     * words recorded in that year. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word) {
        if (!wordFrequencyMap.containsKey(word)) {
            return new TimeSeries();
        } else {
            TimeSeries wordRelativeFreqTS = new TimeSeries();
            TimeSeries originalWordTS = wordFrequencyMap.get(word);

            for (int eachYear : yearlyWordCountMap.keySet()) {
                if (originalWordTS.containsKey(eachYear)) {

                    long totalYearlyCount = yearlyWordCountMap.get(eachYear);
                    double thisWordCountThisYear = originalWordTS.get(eachYear);

                    if (totalYearlyCount != 0) {
                        wordRelativeFreqTS.put(eachYear, thisWordCountThisYear / totalYearlyCount);
                    }
                }
            }
            return wordRelativeFreqTS;
        }
    }

    /**
     * Provides the summed relative frequency per year of all words in WORDS between STARTYEAR and
     * ENDYEAR, inclusive of both ends. If a word does not exist in this time frame, ignore it
     * rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words,
                                          int startYear, int endYear) {
        TimeSeries yearSumOfRelativeFreqOfWords = new TimeSeries();

        for (int i = startYear; i <= endYear; i++) {
            if (yearlyWordCountMap.containsKey(i) && yearlyWordCountMap.get(i) != 0) {
                long thisYrNumOfWords = yearlyWordCountMap.get(i);
                double totalFrequency = 0;

                for (String eachWord : words) {
                    if (wordFrequencyMap.containsKey(eachWord)) {
                        TimeSeries wordOriginalTS = wordFrequencyMap.get(eachWord);

                        if (wordOriginalTS.containsKey(i)) {
                            double thisWordFreqThisYear = wordOriginalTS.get(i);
                            totalFrequency += thisWordFreqThisYear / thisYrNumOfWords;
                            yearSumOfRelativeFreqOfWords.put(i, totalFrequency);
                        }
                    }
                }
            }
        }
        return yearSumOfRelativeFreqOfWords;
    }

    /**
     * Returns the summed relative frequency per year of all words in WORDS. If a word does not
     * exist in this time frame, ignore it rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words) {
        TimeSeries yearSumOfRelativeFreqOfWords = new TimeSeries();

        for (int eachYear : yearlyWordCountMap.keySet()) {
            if (yearlyWordCountMap.get(eachYear) != 0) {
                long thisYrNumOfWords = yearlyWordCountMap.get(eachYear);
                double totalFrequency = 0;

                for (String eachWord : words) {
                    if (wordFrequencyMap.containsKey(eachWord)) {
                        TimeSeries wordOriginalTS = wordFrequencyMap.get(eachWord);

                        if (wordOriginalTS.containsKey(eachYear)) {
                            double thisWordFreqThisYear = wordOriginalTS.get(eachYear);
                            totalFrequency += thisWordFreqThisYear / thisYrNumOfWords;
                            yearSumOfRelativeFreqOfWords.put(eachYear, totalFrequency);
                        }
                    }
                }
            }
        }
        return yearSumOfRelativeFreqOfWords;
    }
}
