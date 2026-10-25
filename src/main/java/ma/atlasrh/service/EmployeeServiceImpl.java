
package ma.atlasrh.service;

import ma.atlasrh.entity.Department;
import ma.atlasrh.entity.Employee;
import ma.atlasrh.exception.BusinessRuleException;
import ma.atlasrh.exception.DuplicateResourceException;
import ma.atlasrh.exception.ResourceNotFoundException;
import ma.atlasrh.repository.ContractRepository;
import ma.atlasrh.repository.DepartmentRepository;
import ma.atlasrh.repository.EmployeeRepository;
import ma.atlasrh.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ContractRepository contractRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public EmployeeServiceImpl(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            ContractRepository contractRepository,
            LeaveRequestRepository leaveRequestRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.contractRepository = contractRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Override
    @Transactional
    public Employee createEmployee(Employee employee) {
        validateEmployee(employee);
        normalizeEmployee(employee);

        if (employeeRepository.existsByMatricule(
                employee.getMatricule())) {
            throw new DuplicateResourceException(
                    "Un employé avec ce matricule existe déjà."
            );
        }

        if (employeeRepository.existsByEmailIgnoreCase(
                employee.getEmail())) {
            throw new DuplicateResourceException(
                    "Un employé avec cet email existe déjà."
            );
        }

        validateRelationships(employee, null);

        return employeeRepository.save(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getEmployeeById(Long id) {
        validateId(id);
        return findEmployeeOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByDepartment(Long departmentId) {
        validateId(departmentId);

        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException(
                    "Département introuvable avec l'identifiant : "
                            + departmentId
            );
        }

        return employeeRepository.findByDepartment_Id(departmentId);
    }

    @Override
    @Transactional
    public Employee updateEmployee(Long id, Employee employee) {
        validateId(id);
        validateEmployee(employee);
        normalizeEmployee(employee);

        Employee existingEmployee = findEmployeeOrThrow(id);

        if (employeeRepository.existsByMatriculeAndIdNot(
                employee.getMatricule(), id)) {
            throw new DuplicateResourceException(
                    "Un employé avec ce matricule existe déjà."
            );
        }

        if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(
                employee.getEmail(), id)) {
            throw new DuplicateResourceException(
                    "Un employé avec cet email existe déjà."
            );
        }

        validateRelationships(employee, id);

        existingEmployee.setMatricule(employee.getMatricule());
        existingEmployee.setFirstName(employee.getFirstName());
        existingEmployee.setLastName(employee.getLastName());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setPosition(employee.getPosition());
        existingEmployee.setHireDate(employee.getHireDate());
        existingEmployee.setDepartment(employee.getDepartment());
        existingEmployee.setManager(employee.getManager());

        return employeeRepository.save(existingEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        validateId(id);

        Employee employee = findEmployeeOrThrow(id);

        if (employeeRepository.existsByManager_Id(id)) {
            throw new BusinessRuleException(
                    "Impossible de supprimer cet employé, "
                            + "car il est responsable d'autres employés."
            );
        }

        if (!contractRepository.findByEmployee_Id(id).isEmpty()) {
            throw new BusinessRuleException(
                    "Impossible de supprimer cet employé, "
                            + "car il possède des contrats."
            );
        }

        if (!leaveRequestRepository.findByEmployee_Id(id).isEmpty()) {
            throw new BusinessRuleException(
                    "Impossible de supprimer cet employé, "
                            + "car il possède des demandes de congé."
            );
        }

        employeeRepository.delete(employee);
    }

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employé introuvable avec l'identifiant : " + id
                ));
    }

    private void validateEmployee(Employee employee) {
        if (employee == null) {
            throw new BusinessRuleException(
                    "Les informations de l'employé sont obligatoires."
            );
        }

        if (employee.getMatricule() == null
                || employee.getMatricule().isBlank()) {
            throw new BusinessRuleException(
                    "Le matricule est obligatoire."
            );
        }

        if (employee.getFirstName() == null
                || employee.getFirstName().isBlank()) {
            throw new BusinessRuleException(
                    "Le prénom est obligatoire."
            );
        }

        if (employee.getLastName() == null
                || employee.getLastName().isBlank()) {
            throw new BusinessRuleException(
                    "Le nom est obligatoire."
            );
        }

        if (employee.getEmail() == null
                || employee.getEmail().isBlank()) {
            throw new BusinessRuleException(
                    "L'email est obligatoire."
            );
        }

        if (employee.getHireDate() == null) {
            throw new BusinessRuleException(
                    "La date d'embauche est obligatoire."
            );
        }
    }

    private void normalizeEmployee(Employee employee) {
        employee.setMatricule(employee.getMatricule().trim());
        employee.setFirstName(employee.getFirstName().trim());
        employee.setLastName(employee.getLastName().trim());
        employee.setEmail(employee.getEmail().trim());

        if (employee.getPosition() != null) {
            employee.setPosition(employee.getPosition().trim());
        }
    }

    private void validateRelationships(
            Employee employee,
            Long currentEmployeeId) {

        Department requestedDepartment = employee.getDepartment();

        if (requestedDepartment == null
                || requestedDepartment.getId() == null) {
            throw new BusinessRuleException(
                    "Le département de l'employé est obligatoire."
            );
        }

        Long departmentId = requestedDepartment.getId();

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Département introuvable avec l'identifiant : "
                                + departmentId
                ));

        employee.setDepartment(department);

        if (employee.getManager() == null) {
            return;
        }

        Long managerId = employee.getManager().getId();

        if (managerId == null) {
            throw new BusinessRuleException(
                    "L'identifiant du responsable est obligatoire."
            );
        }

        if (currentEmployeeId != null
                && currentEmployeeId.equals(managerId)) {
            throw new BusinessRuleException(
                    "Un employé ne peut pas être son propre responsable."
            );
        }

        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Responsable introuvable avec l'identifiant : "
                                + managerId
                ));

        employee.setManager(manager);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessRuleException(
                    "L'identifiant doit être un nombre positif."
            );
        }
    }
}
