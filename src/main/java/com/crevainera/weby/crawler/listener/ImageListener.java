package com.crevainera.weby.crawler.listener;

import com.crevainera.weby.crawler.services.image.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import static com.crevainera.weby.crawler.config.ActiveMQConfiguration.ARTICLE_ID_MESSAGE_QUEUE;

@Component
@Slf4j
public class ImageListener {

    private ImageService imageService;

    @Autowired
    public ImageListener(final ImageService imageService) {
        this.imageService = imageService;
    }

    @JmsListener(destination = ARTICLE_ID_MESSAGE_QUEUE, containerFactory = "jmsFactory")
    public void receiveMessage(final String articleId) {
        log.debug("Message received article.id(" + articleId + ")");
        imageService.process(Long.valueOf(articleId));
        log.debug("Message processed article.id(" + articleId + ")");
    }
}