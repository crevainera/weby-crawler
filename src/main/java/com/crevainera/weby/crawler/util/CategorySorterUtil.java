package com.crevainera.weby.crawler.util;

import com.crevainera.weby.crawler.entities.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Categories sorter
 */
public class CategorySorterUtil {

    private CategorySorterUtil() {
    }

    /**
     * Receives a random mixed {@link Category}'s {@link List} and returns an intermixed per site {@link Category}'s
     * {@link List}
     *
     * @param categoryList {@link List<Category>} random mixed
     *
     * @return {@link List<Category>} intermixed by siteId
     */
    public static List<Category> sortToDistibuteSiteWorkLoadEqually(final List<Category> categoryList) {
        Map<Long, List<Category>> categoryMap = categoryList.stream().collect(Collectors.groupingBy(Category::getSiteId));

        Long[] siteIds = categoryMap.keySet().toArray(new Long[categoryMap.size()]);

        int[] categoriesAddedPerSite = new int[siteIds.length];
        for (int i = 0; i < categoriesAddedPerSite.length; i++) {
            categoriesAddedPerSite[i] = 0;
        }

        List<Category> intermixerCategories = new ArrayList<>();
        int sitesAdded = 0;
        int siteIdIndex = 0;

        while (sitesAdded < siteIds.length) {

            long siteId = siteIds[siteIdIndex];
            if (categoriesAddedPerSite[siteIdIndex] == categoryMap.get(siteId).size()) {
                sitesAdded++;
            } else {
                intermixerCategories.add(categoryMap.get(siteId).get(categoriesAddedPerSite[siteIdIndex]));
                categoriesAddedPerSite[siteIdIndex] = categoriesAddedPerSite[siteIdIndex] + 1;
            }

            siteIdIndex = (siteIdIndex == siteIds.length -1) ? siteIdIndex = 0 : siteIdIndex + 1;
        }

        return intermixerCategories;
    }


}