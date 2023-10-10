package com.sample.multitenant.mongo.repository;

import com.sample.multitenant.mongo.domain.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {
    List<Employee> findByFirstName(String name);
}
