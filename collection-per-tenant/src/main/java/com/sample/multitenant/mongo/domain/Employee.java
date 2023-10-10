package com.sample.multitenant.mongo.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Sharded;


@Data
@NoArgsConstructor
@Document(collection = "#{@appConfig.getCollectionName('Employee')}")
public class Employee {

    @Id
    private String id;
    private String firstName;
    private String lastName;
}
