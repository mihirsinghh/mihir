package main;

import edu.princeton.cs.algs4.In;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;

public class WordNet {
    Graph graph;

    //constructor parses files, creates synset Nodes in the graph, and maps connections between the Nodes
    public WordNet(String synsetFilename, String hyponymsFilename) {
        graph = new Graph();

        In synsetFile = new In(synsetFilename);
        In hyponymsFile = new In(hyponymsFilename);

        //parses the synset file. Adds each new ID-synset Set pairing to the graph's Node map
        while (!synsetFile.isEmpty()) {
            String nextSynsetLine = synsetFile.readLine();
            String[] splitSynsetLine = nextSynsetLine.split(",");
            //splitSynsetLine = ["4", "jump parachuting", "dummy"]

            //initialize the synsetID and the synset
            int synsetID = Integer.parseInt(splitSynsetLine[0]);
            TreeSet<String> synset = new TreeSet<>();

            String synsetWords = splitSynsetLine[1];
            String[] noSpaces = synsetWords.split("\\s");
            //noSpaces = ["jump", "parachuting"]

            //add the words to the synset
            synset.addAll(List.of(noSpaces));

            //places the synsetID into the graph as a new unconnected node (synset with no hyponyms),
            //and updates the graph's library of synsetID-synset pairs
            graph.addNewNode(synsetID, synset);
        }


        while (!hyponymsFile.isEmpty()) {
            String nextHyponymsLine = hyponymsFile.readLine();
            String[] splitHyponymsLine = nextHyponymsLine.split(",");

            int hypernymID = Integer.parseInt(splitHyponymsLine[0]);
            for (int i = 1; i < splitHyponymsLine.length; i++) {
                graph.addEdge(hypernymID, Integer.parseInt(splitHyponymsLine[i]));
            }
        }
        //[5, 6, 7]
    }

    public Set<String> getHyponymsOf(List<String> words) {
        return graph.getHyponymsOf(words);
    }

    public Set<String> getHypernymsOf(List<String> words) {
        return graph.getHypernymsOf(words);
    }

}
