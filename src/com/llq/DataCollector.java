package com.llq;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class DataCollector {
    private Map<String, String> diseases = new HashMap<>();
    private String path;
    private int base, rand;

    DataCollector(String savePath, int baseTime, int randTime) {
        path = savePath;
        base = baseTime;
        rand = randTime;
        collectURLs();
    }

    private void collectURLs() {
        String baseURL = "https://m.dxy.com/diseases";
        String ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1 (KHTML, like Gecko) CriOS/68.0.3440.106 Mobile/13B143 Safari/601.1.46";


        // 模拟手机访问，因为是静态页面可以直接获得全部疾病的url
        // 如果用电脑的话就需要一次次下拉页面
        try {
            Document doc = Jsoup.connect(baseURL)
                    .userAgent(ua)
                    .timeout(3000)
                    .get();
            // 找到各个疾病
            Elements diseaseCards = doc.getElementsByClass("disease-filter-list-card divide");
            for (int i = 0; i < diseaseCards.size(); i++) {
                String diseaseName = diseaseCards.get(i).text();
                String link = diseaseCards.get(i).attr("href") + "/detail";
                diseases.put(diseaseName, link);
            }
        } catch (IOException e) {
            System.out.println("Something went wrong...");
            e.printStackTrace();
        }
    }

    int parseURLs() {
        //等待数据加载的时间
        //为了防止服务器封锁，这里的时间要模拟人的行为，随机且不能太短
        Random random = new Random();
        int randT = random.nextInt(rand);

        // 依次爬每一个链接
        for (Map.Entry<String, String> entry : diseases.entrySet()) {
            // 省略已经爬取的东西
            String fileName = path + entry.getKey() + ".txt";
            File f = new File(fileName);
            if (f.exists())
                continue;
            // 爬取过程
            try {
                Thread.sleep(base + randT);
                Disease content = new Disease(entry.getKey(), entry.getValue(), path);
                content.save2file();
                System.out.println("Disease: " + entry.getKey() + " is captured.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                return -1;
            }
        }
        return 0;
    }
}
