package com.sample.multitenant.mongo.controller;

import com.sample.multitenant.mongo.domain.Employee;
import com.sample.multitenant.mongo.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class EmployeeController {

    private EmployeeService employeeService;

//    @GetMapping(path = "/employee/{id}")
//    public ResponseEntity<Employee> getEmployeeById(@PathVariable("id") final String id) {
//        return new ResponseEntity(employeeService.findById(id), HttpStatus.OK);
//    }

    @GetMapping(path = "/employee/{name}")
    public ResponseEntity<Employee> getEmployeeByName(@PathVariable("name") final String name) {
        return new ResponseEntity(employeeService.findByName(name), HttpStatus.OK);
    }

    @GetMapping(path = "/employee")
    public ResponseEntity<List<Employee>> getEmployees() {
        return new ResponseEntity(employeeService.findAll(), HttpStatus.OK);
    }

    @PostMapping(path = "/employee")
    public ResponseEntity<Employee> createEmployee(@RequestBody final Employee employee) {
        return new ResponseEntity(employeeService.save(employee), HttpStatus.CREATED);
    }
}
