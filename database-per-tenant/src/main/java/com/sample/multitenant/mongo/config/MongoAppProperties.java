package com.sample.multitenant.mongo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "multi-tenant.mongos")
public class MongoAppProperties {

    private String defaultTenant;
    private List<TenantProperties> tenantProperties;

    @Getter
    @Setter
    public static class TenantProperties {
        private String tenant;
        private MongoProperties properties;
    }
}
