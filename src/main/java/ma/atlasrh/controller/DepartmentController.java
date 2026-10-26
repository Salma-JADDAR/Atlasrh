
package ma.atlasrh.controller;

import jakarta.validation.Valid;
import ma.atlasrh.dto.DepartmentDTO;
import ma.atlasrh.entity.Department;
import ma.atlasrh.mapper.DepartmentMapper;
import ma.atlasrh.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentMapper departmentMapper;

    public DepartmentController(
            DepartmentService departmentService,
            DepartmentMapper departmentMapper) {
        this.departmentService = departmentService;
        this.departmentMapper = departmentMapper;
    }

    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(
            @Valid @RequestBody DepartmentDTO departmentDTO) {

        Department department = departmentMapper.toEntity(departmentDTO);
        Department createdDepartment =
                departmentService.createDepartment(department);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(departmentMapper.toDto(createdDepartment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(
            @PathVariable Long id) {

        Department department = departmentService.getDepartmentById(id);

        return ResponseEntity.ok(departmentMapper.toDto(department));
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {

        List<DepartmentDTO> departments = departmentService
                .getAllDepartments()
                .stream()
                .map(departmentMapper::toDto)
                .toList();

        return ResponseEntity.ok(departments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentDTO departmentDTO) {

        Department department = departmentMapper.toEntity(departmentDTO);
        Department updatedDepartment =
                departmentService.updateDepartment(id, department);

        return ResponseEntity.ok(departmentMapper.toDto(updatedDepartment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(
            @PathVariable Long id) {

        departmentService.deleteDepartment(id);

        return ResponseEntity.noContent().build();
    }
}
