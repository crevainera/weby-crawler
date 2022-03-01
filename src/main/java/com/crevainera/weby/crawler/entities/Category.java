package com.crevainera.weby.crawler.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Setter
@Getter
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    private String url;
    private Boolean enabled;
    @Column(name = "site_id")
    private long siteId;
    @Transient
    private Site site;

    @JsonIgnore
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    private ScrapRule scrapRule;

    @JsonIgnore
    @ManyToOne(
            fetch = FetchType.EAGER
    )
    private Label label;
}
