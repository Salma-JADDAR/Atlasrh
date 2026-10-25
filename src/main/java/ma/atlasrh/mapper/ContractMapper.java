package ma.atlasrh.mapper;

import ma.atlasrh.dto.ContractDTO;
import ma.atlasrh.entity.Contract;
import ma.atlasrh.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContractMapper {

    @Mapping(target = "employeeId", source = "employee.id")
    ContractDTO toDto(Contract contract);

    @Mapping(target = "id", ignore = true)
    @Mapping(
            target = "employee",
            expression = "java(toEmployee(contractDTO.employeeId()))"
    )
    Contract toEntity(ContractDTO contractDTO);

    default Employee toEmployee(Long employeeId) {
        if (employeeId == null) {
            return null;
        }

        Employee employee = new Employee();
        employee.setId(employeeId);
        return employee;
    }
}