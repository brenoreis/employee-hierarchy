package com.breno.employeehierarchy.service;

import com.breno.employeehierarchy.exception.ValidationException;
import com.breno.employeehierarchy.model.Employee;
import com.breno.employeehierarchy.repository.EmployeeRepository;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


/**
 * Created by breno.pinto on 22/6/17.
 */
@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee getEmployeeHierarchy(String payload) throws ValidationException {
            Table<String, String, String> employeeTable = processInput(payload);
            saveEmployees(employeeTable, "");
            validateEmployees(employeeTable);
            return getHierarchy();
    }

    private void validateEmployees(Table<String, String, String> employeeTable) throws ValidationException {
        Long count = employeeRepository.count();
        if (count != employeeTable.rowKeySet().size()) {
            throw new ValidationException("An employee has an invalid manager.");
        }
    }

    private void saveEmployees(Table<String, String, String> employeeTable, String managerId) throws ValidationException {

        Map<String, String> employeeDetails = employeeTable.column(managerId);

        for (Map.Entry<String, String> entry : employeeDetails.entrySet()) {
            Employee manager = null;
            if (!"".equals(managerId)) {
                manager = employeeRepository.findById(Long.parseLong(managerId));
                if (manager == null) {
                    throw new ValidationException("Manager not found: " + managerId);
                }
            }
            Employee employee = new Employee(Long.parseLong(entry.getValue()),entry.getKey(), manager);
            employeeRepository.save(employee);
            if (manager != null) {
                manager.getEmployees().add(employee);
                employeeRepository.save(manager);
            }
            saveEmployees(employeeTable, entry.getValue());
        }

    }

    private Employee getHierarchy() {
        //returns the ceo
        return employeeRepository.findByManager(null);
    }

    private Table<String, String, String> processInput(String payload) throws ValidationException {
        boolean ceoFlag = false;
        String[] employees = payload.split("\n");
        Table<String, String, String> employeeTable = HashBasedTable.create();

        for (String employeeString : employees) {
            String[] employeeDetails = employeeString.split(";");
            if (employeeDetails.length != 3) {
                if (ceoFlag) {
                    throw new ValidationException("There can be only one CEO:" + employeeString);
                }
                ceoFlag = true;

                if (employeeDetails.length < 2 || employeeDetails.length > 3) {
                    throw new ValidationException("Employee record is invalid: " + employeeString);
                }
            }
            addEmployeeRecord(employeeTable, employeeDetails);
        }
        return employeeTable;
    }

    private void addEmployeeRecord(Table<String, String, String> employeeTable, String[] employeeDetails) throws ValidationException {

        String name = employeeDetails[0];

        try {
            Long.parseLong(employeeDetails[1]);
        } catch (NumberFormatException nfe) {
            throw new ValidationException("Invalid id: " + employeeDetails[1]);
        }
        String id = employeeDetails[1];

        String managerId = "";
        if (employeeDetails.length == 3) {
            try {
                Long.parseLong(employeeDetails[2]);
            } catch (NumberFormatException nfe) {
                throw new ValidationException("Invalid manager id: " + employeeDetails[2]);
            }
            managerId = employeeDetails[2];
        }
        employeeTable.put(name, managerId, id);
    }
}