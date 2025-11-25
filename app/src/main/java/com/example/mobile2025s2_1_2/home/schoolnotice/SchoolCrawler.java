package com.example.mobile2025s2_1_2.home.schoolnotice;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class SchoolCrawler {

    public static List<SchoolCardData> fetchNotices() {
        List<SchoolCardData> result = new ArrayList<>();

        try {
            String URL = "https://www.kookmin.ac.kr/user/kmuNews/notice/4/index.do";

            // 웹 요청 (파이썬의 requests.get 대체)
            Document doc = Jsoup.connect(URL)
                    .userAgent("Mozilla/5.0")
                    .get();

            // Python soup.select() 와 동일
            Elements links = doc.select("td.tit a, td.tit div a, a[href*=/user/kmuNews/notice/4/]");

            for (Element a : links) {
                String href = a.attr("href");
                if (href == null || href.startsWith("javascript")) continue;

                String title = a.text().trim();
                String fullUrl = a.absUrl("href");

                result.add(new SchoolCardData(title, fullUrl));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}