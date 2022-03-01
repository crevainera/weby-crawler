package com.crevainera.weby.crawler.config;

import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import javax.jms.ConnectionFactory;

@Configuration
public class ActiveMQConfiguration {

    public final static String ARTICLE_ID_MESSAGE_QUEUE_FOR_THUMB_IMAGES = "article-id-message-queue";

    @Bean
    public JmsListenerContainerFactory<?> jmsFactory(final ConnectionFactory connectionFactory,
                                                     final DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        return factory;
    }
}
