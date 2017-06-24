package com.breno.employeehierarchy.service;

import com.breno.employeehierarchy.exception.ValidationException;
import com.breno.employeehierarchy.model.Employee;
import com.breno.employeehierarchy.repository.EmployeeRepository;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


/**
 * Created by breno.pinto on 22/6/17.
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee getEmployeeHierarchy(String payload) throws ValidationException {
        employeeRepository.deleteAllInBatch();
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
        boolean ceoFlag = false;

        for (Map.Entry<String, String> entry : employeeDetails.entrySet()) {
            Employee manager = null;
            if (!"".equals(managerId)) {
                manager = employeeRepository.findById(Long.parseLong(managerId));
                if (manager == null) {
                    throw new ValidationException("Manager not found: " + managerId);
                }
            } else {
                if (ceoFlag) {
                    throw new ValidationException("There can be only one CEO. Id: " + entry.getValue());
                }
                ceoFlag = true;
            }
            Employee employee = new Employee(Long.parseLong(entry.getValue()), entry.getKey(), manager);
            try {
                employeeRepository.save(employee);
            } catch (InvalidDataAccessApiUsageException ex) {
                throw new ValidationException("Duplicate id: " + entry.getValue());
            }
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
        String[] employees = payload.split("\n");
        Table<String, String, String> employeeTable = HashBasedTable.create();

        for (String employeeString : employees) {
            String[] employeeDetails = employeeString.split(";", -1);
            if (employeeDetails.length != 3) {
                throw new ValidationException("Employee record is invalid: " + employeeString);
            }
            addEmployeeRecord(employeeTable, employeeDetails);
        }
        return employeeTable;
    }

    private void addEmployeeRecord(Table<String, String, String> employeeTable, String[] employeeDetails) throws ValidationException {

        String name = employeeDetails[0].trim();

        try {
            Long.parseLong(employeeDetails[1].trim());
        } catch (NumberFormatException nfe) {
            throw new ValidationException("Invalid id: " + employeeDetails[1]);
        }
        String id = employeeDetails[1].trim();

        String managerId = "";
        if (!employeeDetails[2].trim().equals(managerId)) {
            try {
                Long.parseLong(employeeDetails[2].trim());
            } catch (NumberFormatException nfe) {
                throw new ValidationException("Invalid manager id: " + employeeDetails[2]);
            }
            managerId = employeeDetails[2].trim();
        }
        employeeTable.put(name, managerId, id);
    }
}