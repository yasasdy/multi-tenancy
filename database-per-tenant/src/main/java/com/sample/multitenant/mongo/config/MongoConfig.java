package com.sample.multitenant.mongo.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

@EnableConfigurationProperties(value = MongoAppProperties.class)
@AutoConfigureBefore(org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration.class)
@Configuration
public class MongoConfig {

    @Bean
    public MultiTenantConfig multiTenantMongoConfig(final MongoAppProperties mongoAppProperties) {
        return new MultiTenantConfig(mongoAppProperties);
    }

    @Bean
    @Primary
    public MongoDatabaseFactory mongoDatabaseFactory(final MultiTenantConfig multiTenantConfig) {
        return new MongoDbFactory(multiTenantConfig);
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate(final MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}
