package com.sample.multitenant.mongo.repository;

import com.sample.multitenant.mongo.domain.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Employee findByFirstName(String firstName);
}
