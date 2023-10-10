package com.sample.multitenant.mongo.service;

import com.sample.multitenant.mongo.domain.Employee;
import com.sample.multitenant.mongo.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    public Employee findByName(String id) {
        return employeeRepository.findByFirstName(id);
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }
}
