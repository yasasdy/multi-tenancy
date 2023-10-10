package com.sample.multitenant.mongo.config;

import com.mongodb.client.MongoDatabase;
import com.sample.multitenant.mongo.context.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

public class MongoDbFactory extends SimpleMongoClientDatabaseFactory {

    private final MultiTenantConfig multiTenantConfig;

    public MongoDbFactory(final MultiTenantConfig multiTenantConfig) {
        super(multiTenantConfig.getMultiTenantConfig().get("DEFAULT").getMongoClient(),
                multiTenantConfig.getMultiTenantConfig().get("DEFAULT").getDatabase());
        this.multiTenantConfig = multiTenantConfig;
    }

    @Override
    public MongoDatabase getMongoDatabase() throws DataAccessException {
        final String tenant = TenantContext.getTenantId();
        if (tenant != null) {
            final TenantMongoClient tenantMongoClient = multiTenantConfig.getMultiTenantConfig().get(tenant);
            if (tenantMongoClient == null) {
                throw new RuntimeException("Tenant not found " + tenant);
            }
            return tenantMongoClient.getMongoClient().getDatabase(tenantMongoClient.getDatabase());
        } else
            return getMongoClient().getDatabase(getDefaultDatabaseName());
    }

    @Override
    public void destroy() throws Exception {
        multiTenantConfig.getMultiTenantConfig().values().forEach(mongo -> mongo.getMongoClient().close());
    }
}
