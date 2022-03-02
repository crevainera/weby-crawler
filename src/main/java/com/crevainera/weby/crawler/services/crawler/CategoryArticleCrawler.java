package com.crevainera.weby.crawler.services.crawler;

import com.crevainera.weby.crawler.dto.HeadLineDto;
import com.crevainera.weby.crawler.entities.*;
import com.crevainera.weby.crawler.exception.WebyException;
import com.crevainera.weby.crawler.repositories.ArticleRepository;
import com.crevainera.weby.crawler.services.html.HtmlDocumentConnector;
import com.crevainera.weby.crawler.services.scraper.HeadlineListScraper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.crevainera.weby.crawler.config.ActiveMQConfiguration.ARTICLE_ID_MESSAGE_QUEUE_FOR_THUMB_IMAGES;
import static com.crevainera.weby.crawler.constant.WebyConstant.CRAWLER_ERROR;

/**
 * Creates articles from headlines for a given category of a site and updates Articles table
 */
@Service
@Slf4j
public class CategoryArticleCrawler {

    public static final String HTTP_PROTOCOL = "http";

    private JmsTemplate jmsTemplate;
    private HtmlDocumentConnector documentFromHtml;
    private ArticleRepository articleRepository;
    private HeadlineListScraper headlineListScraper;

    @Autowired
    public CategoryArticleCrawler(final JmsTemplate jmsTemplate,
                                  final HtmlDocumentConnector documentFromHtml,
                                  final ArticleRepository articleRepository,
                                  final HeadlineListScraper headlineListScraper) {
        this.jmsTemplate = jmsTemplate;
        this.documentFromHtml = documentFromHtml;
        this.articleRepository = articleRepository;
        this.headlineListScraper = headlineListScraper;
    }

    public void crawlCategory(Category category) {

        log.debug("crawling category: " + category.getUrl());

        getNewHeadlines(category).forEach(headLine -> {
                Optional<Article> article = getArticleFromDatabase(headLine.getUrl());
                if (article.isPresent()) {
                    article.get().getLabelList().add(category.getLabel());
                    articleRepository.save(article.get());
                } else {
                    Article newArticle = createNewArticle(category, headLine);
                    articleRepository.save(newArticle);

                    if (category.getSite().getScrapThumbEnabled() && StringUtils.isNotBlank(newArticle.getThumbUrl())) {
                        jmsTemplate.convertAndSend(ARTICLE_ID_MESSAGE_QUEUE_FOR_THUMB_IMAGES, newArticle.getId());
                    }
                }
            });
    }

    private Optional<Article> getArticleFromDatabase(final String url) {
        return Optional.ofNullable(articleRepository.findByUrl(url));
    }

    private List<HeadLineDto> getNewHeadlines(final Category category) {
        try {
            final Document categoryPage = documentFromHtml.getDocument(category.getUrl());
            final List<HeadLineDto> pageHeadLines = headlineListScraper.getHeadLines(categoryPage, category.getScrapRule());
            List<String> databaseUrls = getCategoryUrlsFromDatabase(category, pageHeadLines.size());

            return pageHeadLines.stream().filter(headLine -> !databaseUrls.contains(headLine.getUrl()))
                        .collect(Collectors.toList());
        } catch (WebyException e) {
            log.error(String.format(CRAWLER_ERROR.getMessage(), e.getMessage(), category.getUrl()));
        }

        return Collections.emptyList();
    }

    private List<String> getCategoryUrlsFromDatabase(final Category category, final int size) {
        Slice<Article> articleSlice = articleRepository.findBySiteAndLabelList(category.getSite(), category.getLabel(),
                PageRequest.of(1, size));

        if (articleSlice != null) {
            return articleSlice.stream().map(article -> article.getUrl())
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private Article createNewArticle(final Category category, final HeadLineDto headLineDto) {
        Article article = new Article();
        article.setTitle(headLineDto.getTitle());
        article.setUrl(headLineDto.getUrl());
        getFullThumbUrl(category.getSite(), headLineDto.getThumbUrl()).ifPresent(article::setThumbUrl);
        article.setScrapDate(new Date());
        article.setSite(category.getSite());

        if (!article.getLabelList().contains(category.getLabel())) {
            article.getLabelList().add(category.getLabel());
        }

        return article;
    }

    private Optional<String> getFullThumbUrl(final Site site, final String url) {
        if (site.getScrapThumbEnabled() && (StringUtils.isNotBlank(url))) {
            if (url.startsWith(HTTP_PROTOCOL)) {
                return Optional.of(url);
            } else {
                return Optional.of(site.getUrl() + url);
            }
        }

        return Optional.empty();
    }
}
