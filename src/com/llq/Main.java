package com.llq;

public class Main {

    public static void main(String[] args) {
        DataSearcher searcher = new DataSearcher();
        int result;

        try {
            do {
                result = searcher.Search();
            } while (result == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Okay, bye.");
    }
}
