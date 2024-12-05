package com.ljh.task.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.ljh.task.domain.Employee;
import com.ljh.task.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public Page<Employee> findAll(int page, int size) {
        return employeeRepository.findAll(Pageable.ofSize(size).withPage(page));
    }

    public List<Employee> findAllByName(String name) {
        return employeeRepository.findAllByName(name);
    }

    public void save(Employee employee) {
        employeeRepository.save(employee);
    }

    public void saveAll(List<Employee> employeeList) {
        employeeRepository.saveAll(employeeList);
    }

}
