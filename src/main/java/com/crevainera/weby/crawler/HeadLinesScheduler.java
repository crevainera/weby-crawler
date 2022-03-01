package com.crevainera.weby.crawler;

import com.crevainera.weby.crawler.services.crawler.SiteCategoryCrawler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class HeadLinesScheduler {

    private SiteCategoryCrawler crawlerSitePool;

    @Autowired
    public HeadLinesScheduler(final SiteCategoryCrawler crawlerSitePool) {
        this.crawlerSitePool = crawlerSitePool;
    }

    @PostConstruct
    public void onStartup() {
        crawlerSitePool.run();
    }


    @Scheduled(cron = "${crawler.cron.expression}")
    public void executePeriodically() {
        log.info("executePeriodically");
        crawlerSitePool.run();
    }
}
