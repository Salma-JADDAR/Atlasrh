package ma.atlasrh.service;

import ma.atlasrh.entity.Department;
import ma.atlasrh.exception.BusinessRuleException;
import ma.atlasrh.exception.DuplicateResourceException;
import ma.atlasrh.exception.ResourceNotFoundException;
import ma.atlasrh.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(
            DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional
    public Department createDepartment(Department department) {
        if (department == null) {
            throw new BusinessRuleException(
                    "Le département est obligatoire."
            );
        }

        String name = department.getName();

        if (name == null || name.isBlank()) {
            throw new BusinessRuleException(
                    "Le nom du département est obligatoire."
            );
        }

        department.setName(name.trim());

        if (departmentRepository.existsByNameIgnoreCase(
                department.getName())) {
            throw new DuplicateResourceException(
                    "Un département avec ce nom existe déjà."
            );
        }

        return departmentRepository.save(department);
    }

    @Override
    @Transactional(readOnly = true)
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Département introuvable avec l'identifiant : " + id
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
}