package com.sample.multitenant.mongo.service;

import com.sample.multitenant.mongo.domain.Employee;
import com.sample.multitenant.mongo.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    public Employee findById(String id) {
        return employeeRepository.findById(id).get();
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public List<Employee> findByName(String name) {
        return employeeRepository.findByFirstName(name);
    }
}
