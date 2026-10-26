
package ma.atlasrh.controller;

import jakarta.validation.Valid;
import ma.atlasrh.dto.EmployeeDTO;
import ma.atlasrh.entity.Employee;
import ma.atlasrh.mapper.EmployeeMapper;
import ma.atlasrh.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;

    public EmployeeController(
            EmployeeService employeeService,
            EmployeeMapper employeeMapper) {
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(
            @Valid @RequestBody EmployeeDTO employeeDTO) {

        Employee employee = employeeMapper.toEntity(employeeDTO);
        Employee createdEmployee =
                employeeService.createEmployee(employee);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeMapper.toDto(createdEmployee));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(
            @PathVariable Long id) {

        Employee employee = employeeService.getEmployeeById(id);

        return ResponseEntity.ok(employeeMapper.toDto(employee));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {

        List<EmployeeDTO> employees = employeeService.getAllEmployees()
                .stream()
                .map(employeeMapper::toDto)
                .toList();

        return ResponseEntity.ok(employees);
    }

    @GetMapping("/by-department/{departmentId}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByDepartment(
            @PathVariable Long departmentId) {

        List<EmployeeDTO> employees =
                employeeService.getEmployeesByDepartment(departmentId)
                        .stream()
                        .map(employeeMapper::toDto)
                        .toList();

        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeDTO employeeDTO) {

        Employee employee = employeeMapper.toEntity(employeeDTO);
        Employee updatedEmployee =
                employeeService.updateEmployee(id, employee);

        return ResponseEntity.ok(employeeMapper.toDto(updatedEmployee));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id) {

        employeeService.deleteEmployee(id);

        return ResponseEntity.noContent().build();
    }
}
