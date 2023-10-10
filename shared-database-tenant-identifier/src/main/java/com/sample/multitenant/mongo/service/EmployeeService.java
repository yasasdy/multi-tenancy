package com.sample.multitenant.mongo.service;

import com.sample.multitenant.mongo.context.TenantContext;
import com.sample.multitenant.mongo.domain.Employee;
import com.sample.multitenant.mongo.repository.EmployeeRepository;
import com.sample.multitenant.mongo.util.TenantAwareMongoTemplate;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    TenantAwareMongoTemplate tenantAwareMongoTemplate;

    public Employee findById(String id) {
        return employeeRepository.findById(id).get();
    }

    public Object save(Employee employee) {
        employee.setTenantId(TenantContext.getTenantId());
        return employeeRepository.save(employee);
    }

    public List<Employee> findAll() {
        Query query = new Query();
        return tenantAwareMongoTemplate.find(query, Employee.class);
    }

    public List<Employee> findByName(String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("firstName").is(name));
        return tenantAwareMongoTemplate.find(query, Employee.class);
    }
}
