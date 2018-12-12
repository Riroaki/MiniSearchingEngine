package com.llq;

public class Main {
    public static void main(String[] args) {
        String dataPath = "./data/raw/",
                indexPath = "./data/index/",
                historyPath = "./data/history";
        int baseTime = 2000, randTime = 4000;

        // 收集数据并保存
        DataCollector collector = new DataCollector(dataPath, baseTime, randTime);
        int collectResult;
        do {
            collectResult = collector.parseURLs();
        } while (collectResult != 0);
        System.out.println("数据采集完毕");

        // 解析数据并建立索引
        DataIndexer indexer = new DataIndexer(indexPath, dataPath);
        int indexResult;
        do {
            indexResult = indexer.buildIndex();
        } while (indexResult != 0);
        System.out.println("索引建立完毕");

        // 根据索引查询
        DataSearcher searcher = new DataSearcher(historyPath, indexPath);
        int queryResult;
        try {
            do {
                queryResult = searcher.Search();
            } while (queryResult == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 结束查询
        System.out.println("Okay, bye.");
    }
}
