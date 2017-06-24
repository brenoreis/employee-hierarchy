package com.breno.employeehierarchy.repository;

import com.breno.employeehierarchy.model.Employee;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * Created by breno.pinto on 22/6/17.
 */
public interface EmployeeRepository extends Repository<Employee, Long> {

    Employee findById(Long id);

    Employee findByManager(Employee employee);

    Employee save(Employee employee);

    List<Employee> findAll();

    Long count();

    void deleteAllInBatch();
}
