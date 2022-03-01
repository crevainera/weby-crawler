package com.crevainera.weby.crawler.util;

import com.crevainera.weby.crawler.entities.Category;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit test for {@link CategorySorterUtil}
 */
class CategorySorterUtilTest {

    /**
     * Scenario:
     * Executes {@link CategorySorterUtil#sortToDistibuteSiteWorkLoadEqually(List)} with a random mixed {@link List<Category>}
     *
     * Expectation:
     * Should return a {@link List<Category>} intermixed by siteId
     */
    @Test
    void whenRandomMixedCategoriesAreProvidedShouldReturnsIntermixedCategoriesPerSite() {
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(createCategoryStub(2));
        categoryList.add(createCategoryStub(5));
        categoryList.add(createCategoryStub(5));
        categoryList.add(createCategoryStub(2));
        categoryList.add(createCategoryStub(4));
        categoryList.add(createCategoryStub(2));
        categoryList.add(createCategoryStub(9));
        categoryList.add(createCategoryStub(4));

        List<Category> categoryResults = CategorySorterUtil.sortToDistibuteSiteWorkLoadEqually(categoryList);

        assertNotNull(categoryResults);
        assertEquals(8, categoryList.size());
        assertEquals(2L, categoryResults.get(0).getSiteId());
        assertEquals(4L, categoryResults.get(1).getSiteId());
        assertEquals(5L, categoryResults.get(2).getSiteId());
        assertEquals(9L, categoryResults.get(3).getSiteId());
        assertEquals(2L, categoryResults.get(4).getSiteId());
        assertEquals(4L, categoryResults.get(5).getSiteId());
        assertEquals(5L, categoryResults.get(6).getSiteId());
        assertEquals(2L, categoryResults.get(7).getSiteId());
    }

    /**
     * Creates a {@link Category} with the given {@link long} siteId
     *
     * @param siteId {@link long}
     * @return {@link Category} with siteId set
     */
    private Category createCategoryStub(final long siteId) {
        Category category = new Category();
        category.setSiteId(siteId);

        return category;
    }
}