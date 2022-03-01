package com.crevainera.weby.crawler.services.crawler;

import com.crevainera.weby.crawler.dto.HeadLineDto;
import com.crevainera.weby.crawler.entities.*;
import com.crevainera.weby.crawler.exception.WebyException;
import com.crevainera.weby.crawler.repositories.ArticleRepository;
import com.crevainera.weby.crawler.services.html.HtmlDocumentConnector;
import com.crevainera.weby.crawler.services.scraper.HeadlineListScraper;
import com.google.common.collect.Lists;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jms.core.JmsTemplate;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link CategoryArticleCrawler}
 */
class CategoryArticleCrawlerTest {

    @Mock
    private JmsTemplate jmsTemplate;
    @Mock
    private HtmlDocumentConnector documentFromHtml;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private HeadlineListScraper headlineListScraper;
    @Mock
    private Document document;
    @Mock
    private ScrapRule scrapRule;

    private Site site;
    private Category category;
    private Label label;
    private CategoryArticleCrawler categoryArticleCrawler;


    @BeforeEach
    void setup() throws WebyException {
        MockitoAnnotations.initMocks(this);

        final String category1Url = "http://site.com/category1.html";

        site = new Site();
        site.setTitle("Site1");
        site.setScrapThumbEnabled(true);

        label = new Label();

        category = new Category();
        category.setUrl(category1Url);
        category.setLabel(label);
        category.setSiteId(site.getId());
        category.setSite(site);
        category.setScrapRule(scrapRule);

        doReturn(document).when(documentFromHtml).getDocument(category1Url);

        categoryArticleCrawler = new CategoryArticleCrawler(jmsTemplate, documentFromHtml,
                articleRepository, headlineListScraper);
    }

    @Test
    void whenScraperReturnsTwoHeadlinesThereAreNotArticlesForTheCategoryShouldSaveTwoNewArticles() throws WebyException {

        doReturn(Arrays.asList(createHeadLineStubDto(1L), createHeadLineStubDto(2L)))
                .when(headlineListScraper).getHeadLines(document, scrapRule);

        categoryArticleCrawler.crawlCategory(category);

        verify(articleRepository, times(2)).save(any(Article.class));
    }

    @Test
    void whenAnArticleIsStoredWithTheSameCategoryShouldNotBeSavedOrUpdatedAgain() throws WebyException {

        HeadLineDto headLineDto1 = createHeadLineStubDto(1L);

        doReturn(Arrays.asList(headLineDto1))
                .when(headlineListScraper).getHeadLines(document, scrapRule);

        Article storedArticle1 = new Article();
        storedArticle1.setUrl(headLineDto1.getUrl());
        List<Article> storedArticles = Arrays.asList(storedArticle1);

        Slice<Article> storedArticleSlice = new SliceImpl<>(storedArticles);

        doReturn(storedArticleSlice).when(articleRepository).findBySiteAndLabelList(eq(site), eq(category.getLabel()),
                any(PageRequest.class));

        categoryArticleCrawler.crawlCategory(category);

        verify(articleRepository, times(0)).save(any(Article.class));
    }

    @Test
    void whenAnArticleIsStoredWithOtherLabelShouldBeUpdatedToAddNewLabel() throws WebyException {
        HeadLineDto headLineDto1 = createHeadLineStubDto(1L);

        doReturn(Arrays.asList(headLineDto1))
                .when(headlineListScraper).getHeadLines(document, scrapRule);

        Article storedArticle1 = new Article();
        storedArticle1.setUrl(headLineDto1.getUrl());

        Article storedArticleWithOtherCategory = new Article();
        Set<Label> labelSet = new HashSet<>();
        labelSet.add(new Label());
        storedArticleWithOtherCategory.setLabelList(labelSet);

        doReturn(storedArticleWithOtherCategory).when(articleRepository).findByUrl(headLineDto1.getUrl());

        categoryArticleCrawler.crawlCategory(category);

        verify(articleRepository, times(1)).save(any(Article.class));
    }


    private HeadLineDto createHeadLineStubDto(final long id) {
        final long headLine1SiteId = id;
        final String headLine1Title = "Title" + id;
        final String headLine1Description = "Description" + id;
        final String headLine1ThumbUrl = "http://site.com/thumbUrl" + id + ".jpg";
        final String headLine1ArticleUrl = "http://site.com/article" + id + ".html";

        HeadLineDto headLineDto = new HeadLineDto();
        headLineDto.setId(headLine1SiteId);
        headLineDto.setTitle(headLine1Title);
        headLineDto.setDescription(headLine1Description);
        headLineDto.setThumbUrl(headLine1ThumbUrl);
        headLineDto.setUrl(headLine1ArticleUrl);

        return headLineDto;
    }

}