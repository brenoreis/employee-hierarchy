package com.breno.employeehierarchy.service;

import com.breno.employeehierarchy.exception.ValidationException;
import com.breno.employeehierarchy.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.breno.employeehierarchy.util.TestUtil.readFile;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.annotation.DirtiesContext.*;

/**
 * Created by breno.pinto on 22/6/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class EmployeeServiceIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void shouldReturnResult() throws IOException, ValidationException {

        String result = readFile(getClass().getClassLoader().getResource("valid-input.csv").getPath(), Charset.defaultCharset());

        Employee employee = employeeService.getEmployeeHierarchy(result);

        String expectedEmployee = readFile(getClass().getClassLoader().getResource("valid-output.json").getPath(), Charset.defaultCharset());
        String actualEmployee = mapper.writeValueAsString(employee);
        assertThat(expectedEmployee, is(actualEmployee));

    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionWhenManagerIsNotFound() throws IOException, ValidationException {
        String result = readFile(getClass().getClassLoader().getResource("invalid-manager-input.csv").getPath(), Charset.defaultCharset());
        employeeService.getEmployeeHierarchy(result);
    }

}