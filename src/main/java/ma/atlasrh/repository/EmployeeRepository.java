package ma.atlasrh.repository;

import ma.atlasrh.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByMatricule(String matricule);

    boolean existsByEmailIgnoreCase(String email);

    List<Employee> findByDepartment_Id(Long departmentId);
}