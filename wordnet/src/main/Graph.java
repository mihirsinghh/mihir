package main;

import java.util.*;

//each Graph consists of synsets linked to direct hyponyms of each synset
public class Graph extends HashMap<Integer, Set<Integer>> {
    //stores each synsetID - synset pair in the graph
    Map<Integer, TreeSet<String>> nodes = new HashMap<>();

    //Undirected graph can be represented by also mapping each synset to its direct hypernym synsets
    Map<Integer, Set<Integer>> hypernyms = new HashMap<>();

    public Graph() {
        super();
    }

    //adds a new Node to the graph - this Node (representing a synset) does not yet have any associated hyponym synsets
    public void addNewNode(int synsetID, TreeSet<String> synset) {
        //this synset has no known hyponyms or hypernyms yet
        Set<Integer> hyponymsOfSynset = new HashSet<>();
        Set<Integer> hypernymsOfSynset = new HashSet<>();
        this.put(synsetID, hyponymsOfSynset);
        hypernyms.put(synsetID, hypernymsOfSynset);

        //updates the graph's library of synsetID-synset pairs
        this.nodes.put(synsetID, synset);
    }

    //updates the underlying HashMap to reflect new hypernym-hyponym connections
    public void addEdge(int synsetID1, int synsetID2) {
        this.get(synsetID1).add(synsetID2);
        hypernyms.get(synsetID2).add(synsetID1);
    }

    //returns true if the given synsetID is mapped to an empty Set of hyponyms
    //does so by checking whether the Set of hyponyms associated with a synset is empty or not
    public boolean isHypernym(int synsetID) {
        return !this.get(synsetID).isEmpty();
    }


    //returns a Set of all hyponym IDs linked to this synset
    //Notice that the Set includes the given synsetID - the words contained within a synset are hyponyms of one another
    public Set<Integer> allHyponymsOfSynset(int synsetID) {
        Set<Integer> hyponymsSet = new HashSet<>();

        hyponymsSet.add(synsetID);

        //adds all the hyponyms of each of the given synset's direct hyponyms to the return Set
        for (int eachDirectHyponym : this.get(synsetID)) {
            hyponymsSet.addAll(allHyponymsOfSynset(eachDirectHyponym));
        }
        return hyponymsSet;
    }

    //returns a Set of all hypernym IDs linked to this synset
    //also includes the given synsetID
    public Set<Integer> allHypernymsOfSynset(int synsetID) {
        Set<Integer> hypernymsSet = new HashSet<>();
        hypernymsSet.add(synsetID);

        //adds all the direct hypernyms of the given synset to the return Set
        for (int eachDirectHypernym : hypernyms.get(synsetID)) {
            hypernymsSet.addAll(allHypernymsOfSynset(eachDirectHypernym));
        }
        return hypernymsSet;
    }

    //returns a Set of all synset IDs corresponding to all synsets that contain the given word
    public Set<Integer> findSynsetIDs(String word) {
        Set<Integer> returnIDs = new HashSet<>();

        for (int eachSynsetID : this.nodes.keySet()) { //iterates over each synsetID
            for (String eachWord : this.nodes.get(eachSynsetID)) { //iterates over all words in the corresponding synset
                if (eachWord.equals(word)) {
                    returnIDs.add(eachSynsetID);
                }
            }
        }
        return returnIDs;
    }


    //finds all common hyponym words of words in the list
    public Set<String> getHyponymsOf(List<String> words) {
        Set<String> commonHyponymWords = new TreeSet<>();
        boolean firstHypSetAdded = false;

        for (String eachWord : words) {
            //get the synsetIDs corresponding to all synsets that contain the current word
            Set<Integer> synsetIDs = findSynsetIDs(eachWord);
            if (synsetIDs.isEmpty()) {
                return new TreeSet<>();
            }
            Set<Integer> hyponymIDs = new HashSet<>();

            //using each synsetID, get all hyponymIDs of that synsetID
            for (int eachSynsetID : synsetIDs) {
                hyponymIDs.addAll(allHyponymsOfSynset(eachSynsetID));
            }

            //collect all hyponym words of the current word
            Set<String> hyponymWords = new TreeSet<>();
            for (int eachHyponymID : hyponymIDs) {
                hyponymWords.addAll(this.nodes.get(eachHyponymID));
            }

            //only add all hyponym words of first word in the list to common-word Set so it can be used as reference
            if (commonHyponymWords.isEmpty() && !firstHypSetAdded) {
                firstHypSetAdded = true;
                commonHyponymWords.addAll(hyponymWords);

                //for every other word in the list, filter common-word Set to only retain the same hyponym words that
                //appear in the current words' Collection of hyponyms
            } else {
                commonHyponymWords.retainAll(hyponymWords);
            }
        }
        return commonHyponymWords;
    }


    //finds all common hypernym words of words in the list
    public Set<String> getHypernymsOf(List<String> words) {
        Set<String> commonHypernymWords = new TreeSet<>();
        boolean firstHypSetAdded = false;

        for (String eachWord : words) {
            //get the synsetIDs corresponding to all synsets that contain the current word
            Set<Integer> synsetIDs = findSynsetIDs(eachWord);
            if (synsetIDs.isEmpty()) {
                return new TreeSet<>();
            }
            Set<Integer> hypernymIDs = new HashSet<>();

            //using each synsetID, get all hypernymIDs of that synsetID
            for (int eachSynsetID : synsetIDs) {
                hypernymIDs.addAll(allHypernymsOfSynset(eachSynsetID));
            }

            //collect all hypernym words of the current word
            Set<String> hypernymWords = new TreeSet<>();
            for (int eachHypernymID : hypernymIDs) {
                hypernymWords.addAll(this.nodes.get(eachHypernymID));
            }

            //only add all hypernym words of first word in the list to common-word Set so it can be used as reference
            if (commonHypernymWords.isEmpty() && !firstHypSetAdded) {
                commonHypernymWords.addAll(hypernymWords);
                firstHypSetAdded = true;

                //for every other word in the list, filter common-word Set to only retain the same hyponym words that
                //appear in the current words' Collection of hyponyms
            } else {
                commonHypernymWords.retainAll(hypernymWords);
            }
        }
        return commonHypernymWords;
    }
}



