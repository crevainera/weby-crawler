package com.crevainera.weby.crawler;

import com.crevainera.weby.crawler.services.crawler.CategoryArticleCrawler;
import com.crevainera.weby.crawler.services.helper.CategoryHelper;
import com.crevainera.weby.crawler.util.CategorySorterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class CrawlerScheduler {

    private static final String OK = "OK";

    private CategoryHelper categoryHelper;
    private ExecutorService headLinesBySitePoolSize;
    private CategoryArticleCrawler categoryArticleCrawler;

    @Autowired
    public CrawlerScheduler(CategoryHelper categoryHelper, ExecutorService headLinesBySitePoolSize, CategoryArticleCrawler categoryArticleCrawler) {
        this.categoryHelper = categoryHelper;
        this.headLinesBySitePoolSize = headLinesBySitePoolSize;
        this.categoryArticleCrawler = categoryArticleCrawler;
    }

    @PostConstruct
    public void onStartup() {
        runCrawler();
    }

    @Scheduled(cron = "${crawler.cron.expression}")
    public void executePeriodically() {
        runCrawler();
    }

    private void runCrawler() {
        log.debug("crawling Sites");

        CategorySorterUtil.sortToDistibuteSiteWorkLoadEqually(categoryHelper.getAllSitesCategories()).forEach(category -> {
            headLinesBySitePoolSize.submit(() -> {
                log.debug("crawling site:  " + category.getSite().getTitle() + ", category: " + category.getTitle());
                categoryArticleCrawler.crawlCategory(category);

                return OK;
            });
        });
    }
}
