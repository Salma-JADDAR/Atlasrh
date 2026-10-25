package ma.atlasrh.service;

import ma.atlasrh.entity.Contract;

import java.util.List;

public interface ContractService {

    Contract createContract(Contract contract);

    Contract getContractById(Long id);

    List<Contract> getContractsByEmployee(Long employeeId);

    List<Contract> getAllContracts();

    Contract updateContract(Long id, Contract contract);

    void deleteContract(Long id);
}