package com.breno.employeehierarchy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by breno.pinto on 22/6/17.
 */
@Entity
public class Employee implements Serializable {

    public Employee() {
    }

    public Employee(Long id, String name, Employee manager) {
        this.id = id;
        this.name = name;
        this.manager = manager;
    }

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Employee manager;


    @OneToMany(mappedBy = "manager", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Employee> employees;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public List<Employee> getEmployees() {
        if (employees == null) {
            employees = new ArrayList<>();
        }
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}
