
package ma.atlasrh.service;

import ma.atlasrh.entity.Contract;
import ma.atlasrh.entity.Employee;
import ma.atlasrh.exception.BusinessRuleException;
import ma.atlasrh.exception.ResourceNotFoundException;
import ma.atlasrh.repository.ContractRepository;
import ma.atlasrh.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;

    public ContractServiceImpl(
            ContractRepository contractRepository,
            EmployeeRepository employeeRepository) {
        this.contractRepository = contractRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public Contract createContract(Contract contract) {
        validateContract(contract);

        Employee employee = findEmployeeForContract(contract);
        contract.setEmployee(employee);

        return contractRepository.save(contract);
    }

    @Override
    @Transactional(readOnly = true)
    public Contract getContractById(Long id) {
        validateId(id);
        return findContractOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contract> getContractsByEmployee(Long employeeId) {
        validateId(employeeId);

        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException(
                    "Employé introuvable avec l'identifiant : " + employeeId
            );
        }

        return contractRepository.findByEmployee_Id(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    @Override
    @Transactional
    public Contract updateContract(Long id, Contract contract) {
        validateId(id);
        validateContract(contract);

        Contract existingContract = findContractOrThrow(id);
        Employee employee = findEmployeeForContract(contract);

        existingContract.setContractType(contract.getContractType());
        existingContract.setStartDate(contract.getStartDate());
        existingContract.setEndDate(contract.getEndDate());
        existingContract.setGrossSalary(contract.getGrossSalary());
        existingContract.setEmployee(employee);

        return contractRepository.save(existingContract);
    }

    @Override
    @Transactional
    public void deleteContract(Long id) {
        validateId(id);

        Contract contract = findContractOrThrow(id);
        contractRepository.delete(contract);
    }

    private Contract findContractOrThrow(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contrat introuvable avec l'identifiant : " + id
                ));
    }

    private Employee findEmployeeForContract(Contract contract) {
        if (contract.getEmployee() == null
                || contract.getEmployee().getId() == null) {
            throw new BusinessRuleException(
                    "L'employé associé au contrat est obligatoire."
            );
        }

        Long employeeId = contract.getEmployee().getId();

        if (employeeId <= 0) {
            throw new BusinessRuleException(
                    "L'identifiant de l'employé doit être positif."
            );
        }

        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employé introuvable avec l'identifiant : "
                                + employeeId
                ));
    }

    private void validateContract(Contract contract) {
        if (contract == null) {
            throw new BusinessRuleException(
                    "Les informations du contrat sont obligatoires."
            );
        }

        if (contract.getContractType() == null) {
            throw new BusinessRuleException(
                    "Le type de contrat est obligatoire."
            );
        }

        if (contract.getStartDate() == null) {
            throw new BusinessRuleException(
                    "La date de début du contrat est obligatoire."
            );
        }

        if (contract.getEndDate() != null
                && contract.getEndDate().isBefore(contract.getStartDate())) {
            throw new BusinessRuleException(
                    "La date de fin ne peut pas précéder la date de début."
            );
        }

        if (contract.getGrossSalary() == null
                || contract.getGrossSalary().signum() <= 0) {
            throw new BusinessRuleException(
                    "Le salaire brut doit être supérieur à zéro."
            );
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessRuleException(
                    "L'identifiant doit être un nombre positif."
            );
        }
    }
}
