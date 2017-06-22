package com.breno.employeehierarchy.service;

import com.breno.employeehierarchy.exception.ValidationException;
import com.breno.employeehierarchy.model.Employee;
import com.breno.employeehierarchy.repository.EmployeeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.breno.employeehierarchy.util.TestUtil.readFile;

/**
 * Created by breno.pinto on 22/6/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionWhenInputIsNotValid() throws IOException, ValidationException {
        String result = readFile(getClass().getClassLoader().getResource("invalid-input.csv").getPath(), Charset.defaultCharset());
        Employee employee = employeeService.getEmployeeHierarchy(result);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionWhenThereAreTwoCEOs() throws IOException, ValidationException {
        String result = readFile(getClass().getClassLoader().getResource("two-ceos-input.csv").getPath(), Charset.defaultCharset());
        Employee employee = employeeService.getEmployeeHierarchy(result);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionWhenIdIsInvalid() throws IOException, ValidationException {
        String result = readFile(getClass().getClassLoader().getResource("invalid-id-input.csv").getPath(), Charset.defaultCharset());
        Employee employee = employeeService.getEmployeeHierarchy(result);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionWhenManagerIdIsInvalid() throws IOException, ValidationException {
        String result = readFile(getClass().getClassLoader().getResource("invalid-manager-id-input.csv").getPath(), Charset.defaultCharset());
        Employee employee = employeeService.getEmployeeHierarchy(result);
    }

}