package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import ngrams.NGramMap;
import ngrams.TimeSeries;
import org.knowm.xchart.XYChart;
import plotting.Plotter;

import java.util.ArrayList;
import java.util.List;

public class HistoryHandler extends NgordnetQueryHandler {
    NGramMap thisMap;

    public HistoryHandler(NGramMap map) {
        //we need to use "map" to access the weighted history of a word over a particular interval of years
        thisMap = map;
    }

    public String handle(NgordnetQuery q) {
        List<String> words = q.words();
        int startYear = q.startYear();
        int endYear = q.endYear();

        //for each word, obtain the corresponding weightHistory() time series
        //put each time series into a list
        //these time series will be plotted onto the chart

        //each TS is plotted in a different color
        ArrayList<TimeSeries> lts = new ArrayList<>();
        //each TS is assigned the corresponding label given in the "labels" list
        ArrayList<String> labels = new ArrayList<>();

        for (String eachWord : words) {
            TimeSeries wordTS = thisMap.weightHistory(eachWord, startYear, endYear);
            lts.add(wordTS);
            labels.add(eachWord);
        }

        XYChart chart = Plotter.generateTimeSeriesChart(labels, lts);
        String encodedImage = Plotter.encodeChartAsString(chart);

        return encodedImage;
    }
}

