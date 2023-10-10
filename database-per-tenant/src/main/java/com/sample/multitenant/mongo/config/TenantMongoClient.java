package com.sample.multitenant.mongo.config;

import com.mongodb.client.MongoClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TenantMongoClient {
    private MongoClient mongoClient;
    private String database;
}
