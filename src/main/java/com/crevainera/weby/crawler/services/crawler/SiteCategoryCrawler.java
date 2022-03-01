package com.crevainera.weby.crawler.services.crawler;

import com.crevainera.weby.crawler.services.helper.CategoryHelper;
import com.crevainera.weby.crawler.util.CategorySorterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

/**
 * Crawl configured sites and its categories
 */
@Service
@Slf4j
public class SiteCategoryCrawler implements Runnable {

    public static final String OK = "OK";
    private CategoryHelper categoryHelper;
    private ExecutorService headLinesBySitePoolSize;
    private CategoryArticleCrawler categoryArticleCrawler;

    @Autowired
    public SiteCategoryCrawler(final CategoryHelper categoryHelper,
                               final ExecutorService headLinesBySitePoolSize,
                               final CategoryArticleCrawler categoryArticleCrawler) {
        this.categoryHelper = categoryHelper;
        this.headLinesBySitePoolSize = headLinesBySitePoolSize;
        this.categoryArticleCrawler = categoryArticleCrawler;
    }

    @Override
    public void run() {
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
