import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import browser.NgordnetQueryType;
import main.AutograderBuddy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class TestOneWordKNot0Hyponyms {
    public static final String WORDS_FILE = "data/ngrams/frequency-EECS.csv";
    public static final String TOTAL_COUNTS_FILE = "data/ngrams/total_counts.csv";
    public static final String SMALL_SYNSET_FILE = "data/wordnet/synsets-EECS.txt";
    public static final String SMALL_HYPONYM_FILE = "data/wordnet/hyponyms-EECS.txt";

    @Test
    public void testActKNot0() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                WORDS_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words = List.of("CS61A");

        NgordnetQuery nq = new NgordnetQuery(words, 2010, 2020, 4, NgordnetQueryType.HYPONYMS);
        String actual = studentHandler.handle(nq);
        String expected = "[CS170, CS61A, CS61B, CS61C]";
        assertThat(actual).isEqualTo(expected);
    }

    // TODO: Add more unit tests (including edge case tests) here.

    //tests top 5 hypernyms of [food, cake] from 1950-1990 example from spec
    @Test
    public void testSpecFoodCake() {
        String WORD_FILE = "data/ngrams/top_14377_words.csv";
        String COUNTS_FILE = "data/ngrams/total_counts.csv";
        String SYNSET_FILE = "data/wordnet/synsets.txt";
        String HYPONYM_FILE = "data/wordnet/hyponyms.txt";

        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                WORD_FILE, COUNTS_FILE, SYNSET_FILE, HYPONYM_FILE);
        List<String> words = new ArrayList<>();
        words.add("food");
        words.add("cake");

        NgordnetQuery nq = new NgordnetQuery(words, 1950, 1990, 5, NgordnetQueryType.HYPONYMS);
        String actual = studentHandler.handle(nq);
        String expected = "[cake, cookie, kiss, snap, wafer]";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void random() {
        String WORD_FILE = "data/ngrams/top_14377_words.csv";
        String SYNSET_FILE = "data/wordnet/synsets.txt";
        String HYPONYM_FILE = "data/wordnet/hyponyms.txt";

        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                WORD_FILE, TOTAL_COUNTS_FILE, SYNSET_FILE, HYPONYM_FILE);
        List<String> words = new ArrayList<>();
        words.add("pad");
        words.add("movement");
        words.add("set");
        words.add("press");
        words.add("lead");
        words.add("effect");
        words.add("shape");
        words.add("center");
        words.add("right");

        NgordnetQuery nq = new NgordnetQuery(words, 1920, 1980, 8, NgordnetQueryType.HYPONYMS);
        String actual = studentHandler.handle(nq);
        String expected = "[]"; //excludes change, which was published only in 2005
        assertThat(actual).isEqualTo(expected);
    }


    //tests scenario where a hyponym is not included in the given time frame
    @Test
    public void testHyponymNotInTimeFrame() {
        String WORD_FILE = "data/ngrams/test_words_file_synsets_16.txt";
        String SYNSET_FILE = "data/wordnet/synsets16.txt";
        String HYPONYM_FILE = "data/wordnet/hyponyms16.txt";

        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                WORD_FILE, TOTAL_COUNTS_FILE, SYNSET_FILE, HYPONYM_FILE);
        List<String> words = new ArrayList<>();
        words.add("occurrence");

        NgordnetQuery nq = new NgordnetQuery(words, 2008, 2010, 10, NgordnetQueryType.HYPONYMS);
        String actual = studentHandler.handle(nq);
        String expected = "[alteration, increase, occurrence]"; //excludes change, which was published only in 2005
        assertThat(actual).isEqualTo(expected);
    }


    //tests scenario where none of the hyponyms are included in given time frame
    @Test
    public void testNoHyponymsInTimeFrame() {
        String WORD_FILE = "data/ngrams/test_words_file_synsets_16.txt";

        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                WORD_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words = new ArrayList<>();
        words.add("occurrence");

        NgordnetQuery nq = new NgordnetQuery(words, 1990, 1991, 1, NgordnetQueryType.HYPONYMS);
        String actual = studentHandler.handle(nq);
        String expected = "[]";
        assertThat(actual).isEqualTo(expected);
    }


    //tests scenario where k hyponyms are requested, but less than k hyponyms are included in given time frame
    //only hyponyms in given time frame should be included, up to k hyponyms
    @Test
    public void testLessThanKInTimeFrame() {
        String WORD_FILE = "data/ngrams/test_words_file_synsets_16.txt";

        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                WORD_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words = new ArrayList<>();
        words.add("occurrence");

        NgordnetQuery nq = new NgordnetQuery(words, 1990, 1991, 1, NgordnetQueryType.HYPONYMS);
        String actual = studentHandler.handle(nq);
        String expected = "[]";
        assertThat(actual).isEqualTo(expected);
    }

}
