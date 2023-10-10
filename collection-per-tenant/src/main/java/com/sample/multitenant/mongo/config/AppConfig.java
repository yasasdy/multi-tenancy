package com.sample.multitenant.mongo.config;

import com.sample.multitenant.mongo.context.TenantContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    public String getCollectionName(String collection) {
        return collection + '_' + TenantContext.getTenantId();
    }
}
