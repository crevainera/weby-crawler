package com.crevainera.weby.crawler.services.scraper;

import com.crevainera.weby.crawler.exception.WebyException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for {@link HeadlineHtmlScraper}
 */
class HeadlineHtmlScraperTest {

    private static final String SITE_DIRECTORY = "sites/headlines/";
    private static final String SITE_AHORALASFLORES_HTML = SITE_DIRECTORY + "ahoralasflores-9-29-2020-html.test";
    private static final String SITE_NOTICIASLASFLORES_HTML = SITE_DIRECTORY + "noticiaslasflores-9-29-2020-html.test";
    private static final String SITE_PLAY_RADIOS_HTML = SITE_DIRECTORY + "playradios-9-29-2020-html.test";
    private static final String ARTICLE_SCRIPT_NLF_ALF_TEXT = "$input.getElementsByTag(\"article\")";
    private static final String ARTICLE_SCRIPT_PLAY_RADIOS_TEXT = "$input.getElementsByClass(\"td_module_wrap\")";

    private static Stream<Arguments> scrapByRuleAndAssertResultForAhoraLasFlores() {
        return Stream.of(
                Arguments.of(
                        "$input.getElementsByTag(\"h1\").first().getElementsByTag(\"a\").first().textNodes().get(0).text()",
                        "SE INTENSIFICAN LOS CONTROLES DE CIRCULACIÓN"),
                Arguments.of("$input.getElementsByTag(\"h1\").first().getElementsByTag(\"a\").first().attr(\"href\")",
                        "https://ahoralasflores.com.ar/contenido/3719/se-intensifican-los-controles-de-circulacion"),
                Arguments.of("($input.getElementsByTag(\"img\").first()!=null) ? $input.getElementsByTag(\"img\").first().attr(\"data-src\") : \"\"",
                        "/download/multimedia.normal.abd7ffb78bdd1ecd.636c665f6e6f726d616c2e6a7067.jpg")
        );
    }

    private static Stream<Arguments> scrapByRuleAndAssertResultForNoticiasLasFlores() {
        return Stream.of(
                Arguments.of(
                        "$input.getElementsByTag(\"h2\").first().getElementsByTag(\"a\").first().textNodes().get(0).text()",
                        "¿Es feriado puente el próximo lunes 30 de marzo?"),
                Arguments.of("$input.getElementsByTag(\"h2\").first().getElementsByTag(\"a\").first().attr(\"href\")",
                        "https://www.noticiaslasflores.com.ar/69030/nacional/es-feriado-puente-el-proximo-lunes-30-de-marzo/"),
                Arguments.of("($input.getElementsByTag(\"img\").first()!=null) ? $input.getElementsByTag(\"img\").first().attr(\"src\") : \"\"",
                        "https://www.noticiaslasflores.com.ar/wp-content/uploads/2020/03/feriado-calendario.jpg")
        );
    }

    private static Stream<Arguments> scrapByRuleAndAssertResultForPlayRadios() {
        return Stream.of(
                Arguments.of(
                        "$input.getElementsByTag(\"h3\").first().getElementsByTag(\"a\").first().text().intern()",
                        "AMBAPA agradece el apoyo brindado en «La Batalla Final», el Torneo..."),
                Arguments.of("$input.getElementsByTag(\"h3\").first().getElementsByTag(\"a\").first().attr(\"href\")",
                        "http://playradios.com.ar/2020/02/18/ambapa-agradece-el-apoyo-brindado-en-la-batalla-final-el-torneo-nacional-que-se-desarrollo-en-las-flores/"),
                Arguments.of("$input.getElementsByClass(\"td_module_wrap\").first().getElementsByClass(\"entry-thumb\").attr(\"data-img-url\")",
                        "http://playradios.com.ar/wp-content/uploads/2020/02/0c033cfd-9a26-4c8a-8538-8cce50f07994-324x160.jpg")
        );
    }


    private HeadlineHtmlScraper headlineHtmlScraper;


    @BeforeEach
    public void setUp() {
        headlineHtmlScraper = new HeadlineHtmlScraper();
    }

    @ParameterizedTest
    @MethodSource("scrapByRuleAndAssertResultForAhoraLasFlores")
    public void ahoraLasFloresScrapedTextFromFirstHeadlineShouldMatchWithExpectedText(
            final String rule, final String scraped) throws WebyException {
        scrapedTextFromFirstHeadlineShouldMatchWithExpectedText(rule, scraped, SITE_AHORALASFLORES_HTML,
                ARTICLE_SCRIPT_NLF_ALF_TEXT);
    }

    @ParameterizedTest
    @MethodSource("scrapByRuleAndAssertResultForNoticiasLasFlores")
    public void noticiasLasfloresTextFromFirstHeadlineShouldMatchWithExpectedText(
            final String rule, final String scraped) throws WebyException {
        scrapedTextFromFirstHeadlineShouldMatchWithExpectedText(rule, scraped, SITE_NOTICIASLASFLORES_HTML,
                ARTICLE_SCRIPT_NLF_ALF_TEXT);
    }

    @ParameterizedTest
    @MethodSource("scrapByRuleAndAssertResultForPlayRadios")
    public void playRadiosTextFromFirstHeadlineShouldMatchWithExpectedText(
            final String rule, final String scraped) throws WebyException {
        scrapedTextFromFirstHeadlineShouldMatchWithExpectedText(rule, scraped, SITE_PLAY_RADIOS_HTML,
                ARTICLE_SCRIPT_PLAY_RADIOS_TEXT);
    }

    private void scrapedTextFromFirstHeadlineShouldMatchWithExpectedText(final String rule, final String scraped,
                                                                         final String documentUrl,
                                                                         final String articleRule) throws WebyException {
        Document document =getDocument(documentUrl) ;
        Element firstElement = headlineHtmlScraper.getHeadLineElements(document, articleRule).first();
        String scrapedResult = headlineHtmlScraper.getPlainText(firstElement, rule).orElse(StringUtils.EMPTY);

        assertEquals(scraped, scrapedResult);
    }

    public Document getDocument(final String path) {
        URL urlPath = ClassLoader.getSystemResource(path);
        try {
            File input = new File(urlPath.toURI());
            return Jsoup.parse(input, "UTF-8");
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}