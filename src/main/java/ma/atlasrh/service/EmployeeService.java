package ma.atlasrh.service;

import ma.atlasrh.entity.Employee;

import java.util.List;

public interface EmployeeService {

    Employee createEmployee(Employee employee);

    Employee getEmployeeById(Long id);

    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByDepartment(Long departmentId);

    Employee updateEmployee(Long id, Employee employee);

    void deleteEmployee(Long id);
}