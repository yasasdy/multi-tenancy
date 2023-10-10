package com.sample.multitenant.mongo.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class MultiTenantConfig {

    private HashMap<String, TenantMongoClient> multiTenantConfig;
    private final MongoAppProperties mongoAppProperties;

    @PostConstruct
    public void multiTenantMongoConfig() {
        final List<MongoAppProperties.TenantProperties> multiTenantList = mongoAppProperties.getTenantProperties();
        multiTenantConfig = new HashMap<>();

        for (final MongoAppProperties.TenantProperties multiTenant : multiTenantList) {
            final String connectionUri = multiTenant.getProperties().getUri();
            MongoClient client;

            if (connectionUri != null)
                client = MongoClients.create(connectionUri);
            else
                throw new RuntimeException("db config props are missing");

            final String database = multiTenant.getProperties().getDatabase();
            final TenantMongoClient tenantMongoClient = new TenantMongoClient(client, database);
            this.multiTenantConfig.put(multiTenant.getTenant(), tenantMongoClient);

            if (multiTenant.getTenant().equals(mongoAppProperties.getDefaultTenant()))
                multiTenantConfig.put("DEFAULT", tenantMongoClient);
        }
    }

    @PreDestroy
    public void destroy() {
        multiTenantConfig.values().forEach(mongo -> mongo.getMongoClient().close());
    }
}
