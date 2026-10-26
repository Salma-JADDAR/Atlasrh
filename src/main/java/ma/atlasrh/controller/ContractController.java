
package ma.atlasrh.controller;

import jakarta.validation.Valid;
import ma.atlasrh.dto.ContractDTO;
import ma.atlasrh.entity.Contract;
import ma.atlasrh.mapper.ContractMapper;
import ma.atlasrh.service.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractService contractService;
    private final ContractMapper contractMapper;

    public ContractController(
            ContractService contractService,
            ContractMapper contractMapper) {
        this.contractService = contractService;
        this.contractMapper = contractMapper;
    }

    @PostMapping
    public ResponseEntity<ContractDTO> createContract(
            @Valid @RequestBody ContractDTO contractDTO) {

        Contract contract = contractMapper.toEntity(contractDTO);
        Contract createdContract = contractService.createContract(contract);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contractMapper.toDto(createdContract));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractDTO> getContractById(
            @PathVariable Long id) {

        Contract contract = contractService.getContractById(id);

        return ResponseEntity.ok(contractMapper.toDto(contract));
    }

    @GetMapping
    public ResponseEntity<List<ContractDTO>> getAllContracts() {

        List<ContractDTO> contracts = contractService.getAllContracts()
                .stream()
                .map(contractMapper::toDto)
                .toList();

        return ResponseEntity.ok(contracts);
    }

    @GetMapping("/by-employee/{employeeId}")
    public ResponseEntity<List<ContractDTO>> getContractsByEmployee(
            @PathVariable Long employeeId) {

        List<ContractDTO> contracts =
                contractService.getContractsByEmployee(employeeId)
                        .stream()
                        .map(contractMapper::toDto)
                        .toList();

        return ResponseEntity.ok(contracts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContractDTO> updateContract(
            @PathVariable Long id,
            @Valid @RequestBody ContractDTO contractDTO) {

        Contract contract = contractMapper.toEntity(contractDTO);
        Contract updatedContract =
                contractService.updateContract(id, contract);

        return ResponseEntity.ok(contractMapper.toDto(updatedContract));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(
            @PathVariable Long id) {

        contractService.deleteContract(id);

        return ResponseEntity.noContent().build();
    }
}
