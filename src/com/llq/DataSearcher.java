package com.llq;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

class DataSearcher {

    private Scanner s;
    private String historyPath, indexPath;

    DataSearcher(String historyPath, String indexPath) {
        s = new Scanner(System.in);
        this.historyPath = historyPath;
        this.indexPath = indexPath;
        System.out.println(" * * * 欢迎使用迷你搜索引擎 * * *");
        System.out.println("你可以输入任意在这里看到的信息：https://dxy.com/diseases.");
    }

    int Search() throws Exception {
        String q, history;

        System.out.print(">> ");
        if (s.hasNextLine()) {
            q = s.nextLine();
            if (q.length() <= 0) {
                System.out.println("请输入你要查询的内容。");
                return 0;
            } else if ("quit".equals(q))
                return -1;
            else if ("history".equals(q)) {
                int index = 1;
                // 读取历史记录
                try {
                    File hisPath = new File(historyPath);
                    history = new Scanner(hisPath).useDelimiter("\\Z").next();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return -2;
                }
                // 打印历史记录
                for (String his : history.split("\n")) {
                    System.out.print(Integer.toString(index++));
                    System.out.println(": " + his);
                }
                return 0;
            }
        } else
            return 0;

        Directory dir = FSDirectory.open(Paths.get(indexPath)); //  索引所在的位置
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer(); // 标准分词器
        QueryParser parser = new QueryParser("contents", analyzer); // 查询解析器
        Query query = parser.parse(q); // 通过解析要查询的String，获取查询对象

        long startTime = System.currentTimeMillis(); // 记录索引开始时间
        TopDocs docs = searcher.search(query, 100);// 开始查询，查询前n条数据，将记录保存在docs中
        long endTime = System.currentTimeMillis(); // 记录索引结束时间
        System.out.print("查询到" + docs.totalHits + "条记录，");
        System.out.println("共耗时" + (endTime - startTime) + "毫秒");

        if (docs.totalHits == 0)
            return 0;

        int index = 1, fileNum;
        String input;
        String path;
        String[] paths = new String[10];
        while (true) {
            fileNum = 0;
            System.out.println("第" + Integer.toString(index) + "页");
            for (int i = 10 * index - 10; i < docs.scoreDocs.length && i < 10 * index; i++) {
                ScoreDoc scoreDoc = docs.scoreDocs[i];
                Document doc = searcher.doc(scoreDoc.doc); //  scoreDoc.doc相当于docID,根据这个docID来获取文档
                path = doc.get("path");
                paths[fileNum++] = path;
                System.out.print(Integer.toString(index * 10 + fileNum - 10) + ": ");
                System.out.println(path.substring(7, path.length() - 4)); // path是刚刚建立索引的时候我们定义的一个字段
            }
            System.out.println("输入'p'可以看上一页，输入'n'以查看下一页；输入'q'可以退出本次查询；" +
                    "输入某一个索引前的序号可以查看详细内容");
            System.out.print(">> ");
            if (s.hasNextLine()) {
                input = s.nextLine();
                if ("p".equals(input)) {
                    if (index == 1)
                        System.out.println("* 当前已经是第一页");
                    else
                        index--;
                } else if ("n".equals(input)) {
                    if (index == 10)
                        System.out.println("* 当前已经是最后一页（默认保存10页结果）");
                    else
                        index++;
                } else if ("q".equals(input)) {
                    break;
                } else try {
                    int entry = Integer.parseInt(input) - 1;
                    if (entry <= index * 10 && entry >= index * 10 - 10) {
                        File f = new File(paths[entry % 10]);
                        String content = new Scanner(f).useDelimiter("\\Z").next();
                        System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
                        System.out.println(content);
                        System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
                    } else
                        System.out.println("输入编号有误，请重新输入");
                } catch (Exception ignore) {
                    System.out.println("输入信息有误，请重新输入");
                }
            }
        }

        reader.close();
        // 将本次搜索写入历史
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(historyPath, true);
            writer.write(q);
            writer.write("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -2;
        }
        return 0;
    }
}