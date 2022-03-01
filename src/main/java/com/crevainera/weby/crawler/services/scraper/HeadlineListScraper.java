package com.crevainera.weby.crawler.services.scraper;

import com.crevainera.weby.crawler.dto.HeadLineDto;
import com.crevainera.weby.crawler.entities.ScrapRule;
import com.crevainera.weby.crawler.exception.WebyException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creates a {@link HeadLineDto} {@link List} from a {@link Document}
 */
@Service
@Slf4j
public class HeadlineListScraper {

    private HeadlineHtmlScraper scraper;

    @Autowired
    public HeadlineListScraper(final HeadlineHtmlScraper scraper) {
        this.scraper = scraper;
    }

    public List<HeadLineDto> getHeadLines(final Document document, final ScrapRule scrapRule)
            throws WebyException {
        List<HeadLineDto> headLineList = new ArrayList<>();

        scraper.getHeadLineElements(document, scrapRule.getHeadline()).forEach(headline -> {
                headLineList.add(createHeadLineDTO(headline, scrapRule));
            });

        Collections.reverse(headLineList);

        return headLineList;
    }

    private HeadLineDto createHeadLineDTO(final Element htmlHeadline, final ScrapRule scrapRule) {
        HeadLineDto headLine = new HeadLineDto();

        scraper.getPlainText(htmlHeadline, scrapRule.getTitle()).ifPresent(headLine::setTitle);
        scraper.getPlainText(htmlHeadline, scrapRule.getLink()).ifPresent(headLine::setUrl);
        scraper.getPlainText(htmlHeadline, scrapRule.getImage()).ifPresent(headLine::setThumbUrl);

        return headLine;
    }

}
