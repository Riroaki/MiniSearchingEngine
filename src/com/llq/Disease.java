package com.llq;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Disease {
    private String name, url;
    private String[] contents = {"简介:", "\n\n症状:", "\n\n病因:", "\n\n诊断:", "\n\n治疗:", "\n\n生活:", "\n\n预防:"};
    private String fileName;

    Disease(String diseaseName, String diseaseUrl) throws RuntimeException {
        name = diseaseName;
        url = diseaseUrl;
        fileName = "./data/" + name + ".txt";
        String ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1 (KHTML, like Gecko) CriOS/68.0.3440.106 Mobile/13B143 Safari/601.1.46";

        // 爬数据
        try {
            Document doc = Jsoup.connect(diseaseUrl)
                    .userAgent(ua)
                    .timeout(10000)
                    .get();
            Elements details = doc.getElementsByClass("disease-detail-content-box");
            for(int i=0;i<details.size();i++) {
                String content = details.get(i).toString()
                        .replaceAll("<[^><]*>", "")
                        .replaceAll("[ ]", "")
                        .replaceAll("[\n]+", "\n");
                contents[i] += content;
            }
        } catch (IOException e) {
            System.out.println("Oops...your IP has been banned by dxy.com:(");
            throw new RuntimeException("IP", e);
        }
    }

    void save2file() {
        File f = new File(fileName);
        try (OutputStream os = new FileOutputStream(f)) {
            byte[] data;
            for(String entry: contents) {
                data = entry.getBytes(StandardCharsets.UTF_8);
                os.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getURL() {
        return url;
    }
}
