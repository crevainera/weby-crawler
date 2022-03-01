package com.crevainera.weby.crawler.services.helper;

import com.crevainera.weby.crawler.entities.Category;
import com.crevainera.weby.crawler.entities.Site;
import com.crevainera.weby.crawler.repositories.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryHelper {

    private SiteRepository siteRepository;

    @Autowired
    public CategoryHelper(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    public List<Category> getAllSitesCategories() {
        List<Site> sites = siteRepository.findByEnabledTrue();
        List<Category> categories = sites.stream().map(s -> s.getCategoryList())
                .flatMap(List::stream).collect(Collectors.toList());

        categories.forEach(category -> category.setSite(getSiteByCategory(category, sites)));

        return categories;
    }

    private Site getSiteByCategory(final Category category, final List<Site> sites) {
        return sites.stream().filter(s -> s.getId() == category.getSiteId()).findAny().get();
    }
}
