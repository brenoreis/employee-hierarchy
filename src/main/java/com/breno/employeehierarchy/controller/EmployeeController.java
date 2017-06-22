package com.breno.employeehierarchy.controller;

import com.breno.employeehierarchy.exception.ValidationException;
import com.breno.employeehierarchy.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by breno.pinto on 22/6/17.
 */
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @RequestMapping(value = "/employees-hierarchy", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getEmployeeHierarchy(@RequestBody String payload) throws ValidationException {
        try {
            return new ResponseEntity<>(employeeService.getEmployeeHierarchy(payload), HttpStatus.OK);
        } catch (ValidationException ve) {
            return new ResponseEntity<>(ve.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
