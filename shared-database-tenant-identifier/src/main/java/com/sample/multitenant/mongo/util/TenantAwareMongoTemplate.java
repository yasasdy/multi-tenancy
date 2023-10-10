package com.sample.multitenant.mongo.util;

import com.sample.multitenant.mongo.context.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TenantAwareMongoTemplate {

    private MongoTemplate mongoTemplate;

    public TenantAwareMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public <T> List<T> find(Query query, Class<T> entityClass) {
        applyTenantFilter(query);
        return mongoTemplate.find(query, entityClass);
    }


    private void applyTenantFilter(Query query) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
        }
    }
}

