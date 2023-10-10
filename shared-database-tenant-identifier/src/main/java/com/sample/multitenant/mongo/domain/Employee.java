package com.sample.multitenant.mongo.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Sharded;


@Data
@NoArgsConstructor
public class Employee {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String tenantId;
}
