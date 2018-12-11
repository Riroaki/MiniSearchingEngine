package com.llq;


public class Main {

    public static void main(String[] args) {
        DataSearcher searcher = new DataSearcher();
        //[-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]
        String[] test = {"-query", "色弱", "-raw"};
        try {
            searcher.Search(test);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
