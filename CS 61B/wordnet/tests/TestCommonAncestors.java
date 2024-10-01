import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import browser.NgordnetQueryType;
import main.AutograderBuddy;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class TestCommonAncestors {
    public static final String WORDS_FILE = "data/ngrams/very_short.csv";
    public static final String LARGE_WORDS_FILE = "data/ngrams/top_14377_words.csv";
    public static final String TOTAL_COUNTS_FILE = "data/ngrams/total_counts.csv";
    public static final String SMALL_SYNSET_FILE = "data/wordnet/synsets16.txt";
    public static final String SMALL_HYPONYM_FILE = "data/wordnet/hyponyms16.txt";
    public static final String LARGE_SYNSET_FILE = "data/wordnet/synsets.txt";
    public static final String LARGE_HYPONYM_FILE = "data/wordnet/hyponyms.txt";

    /** This is an example from the spec for a common-ancestors query on the word "adjustment".
     * You should add more tests for the other spec examples! */
    @Test
    public void testSpecAdjustment() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                WORDS_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words = List.of("adjustment");

        NgordnetQuery nq = new NgordnetQuery(words, 2000, 2020, 0, NgordnetQueryType.ANCESTORS);
        String actual = studentHandler.handle(nq);
        String expected = "[adjustment, alteration, event, happening, modification, natural_event, occurrence, occurrent]";
        assertThat(actual).isEqualTo(expected);
    }

    // TODO: Add more unit tests (including edge case tests) here.
    @Test
    public void testSpecChange() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                WORDS_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words = List.of("change");

        NgordnetQuery nq = new NgordnetQuery(words, 2000, 2020, 0, NgordnetQueryType.ANCESTORS);
        String actual = studentHandler.handle(nq);
        String expected = "[act, action, alteration, change, event, happening, human_action, human_activity, " +
                "modification, natural_event, occurrence, occurrent]";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void testSpecChangeAdjustment() {
        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                WORDS_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words = new ArrayList<>();
        words.add("change");
        words.add("adjustment");

        NgordnetQuery nq = new NgordnetQuery(words, 2000, 2020, 0, NgordnetQueryType.ANCESTORS);
        String actual = studentHandler.handle(nq);
        String expected = "[alteration, event, happening, modification, natural_event, occurrence, occurrent]";
        assertThat(actual).isEqualTo(expected);
    }


    // TODO: Create similar unit test files for the k != 0 cases.

    //tests fetching of k most popular common hypernyms of single word
    @Test
    public void testKHypernymsSingleWord() {
        String WORD_FILE = "data/ngrams/test_words_file_synsets_16.txt";

        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                WORD_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words = new ArrayList<>();
        words.add("increase");

        NgordnetQuery nq = new NgordnetQuery(words, 2005, 2010, 2, NgordnetQueryType.ANCESTORS);
        String actual = studentHandler.handle(nq);
        String expected = "[change, increase]";
        assertThat(actual).isEqualTo(expected);
    }


    //tests fetching of k most popular common hypernyms of multiple words
    @Test
    public void testKHypernymsMultipleWords() {
        String WORD_FILE = "data/ngrams/test_words_file_synsets_16.txt";

        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                WORD_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words = new ArrayList<>();
        words.add("change");
        words.add("adjustment");

        NgordnetQuery nq = new NgordnetQuery(words, 2005, 2010, 10, NgordnetQueryType.ANCESTORS);
        String actual = studentHandler.handle(nq);
        String expected = "[alteration, occurrence]";
        assertThat(actual).isEqualTo(expected);
    }


    //tests scenario where the k most common popular hypernyms are requested, but not all k hypernyms are included
    //in the specified time frame
    //in this scenario, only the hypernyms that are included in the given time frame should be included
    @Test
    public void testLessThanK() {
        String WORD_FILE = "data/ngrams/test_words_file_synsets_16.txt";

        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                WORD_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words = new ArrayList<>();
        words.add("increase");

        NgordnetQuery nq = new NgordnetQuery(words, 2005, 2010, 10, NgordnetQueryType.ANCESTORS);
        String actual = studentHandler.handle(nq);
        String expected = "[alteration, change, increase, occurrence]";
        assertThat(actual).isEqualTo(expected);
    }

    //tests scenario where none of the hypernyms were included in given time period
    @Test
    public void testNoHypernyms() {
        String WORD_FILE = "data/ngrams/test_words_file_synsets_16.txt";

        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                WORD_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words = new ArrayList<>();
        words.add("increase");

        NgordnetQuery nq = new NgordnetQuery(words, 1990, 2000, 10, NgordnetQueryType.ANCESTORS);
        String actual = studentHandler.handle(nq);
        String expected = "[]";
        assertThat(actual).isEqualTo(expected);
    }

    //tests scenario where the requested word is not in any of the provided files
    @Test
    public void testNotInFiles() {
        String WORD_FILE = "data/ngrams/test_words_file_synsets_16.txt";

        NgordnetQueryHandler studentHandler = AutograderBuddy.getHyponymsHandler(
                WORD_FILE, TOTAL_COUNTS_FILE, SMALL_SYNSET_FILE, SMALL_HYPONYM_FILE);
        List<String> words = new ArrayList<>();
        words.add("kxjjkere");

        NgordnetQuery nq = new NgordnetQuery(words, 1990, 2000, 10, NgordnetQueryType.ANCESTORS);
        String actual = studentHandler.handle(nq);
        String expected = "[]";
        assertThat(actual).isEqualTo(expected);
    }


}
