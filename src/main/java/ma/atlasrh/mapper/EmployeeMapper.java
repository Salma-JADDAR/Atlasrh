package ma.atlasrh.mapper;

import ma.atlasrh.dto.EmployeeDTO;
import ma.atlasrh.entity.Department;
import ma.atlasrh.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "managerId", source = "manager.id")
    EmployeeDTO toDto(Employee employee);

    @Mapping(target = "id", ignore = true)
    @Mapping(
            target = "department",
            expression = "java(toDepartment(employeeDTO.departmentId()))"
    )
    @Mapping(
            target = "manager",
            expression = "java(toManager(employeeDTO.managerId()))"
    )
    Employee toEntity(EmployeeDTO employeeDTO);

    default Department toDepartment(Long departmentId) {
        if (departmentId == null) {
            return null;
        }

        Department department = new Department();
        department.setId(departmentId);
        return department;
    }

    default Employee toManager(Long managerId) {
        if (managerId == null) {
            return null;
        }

        Employee manager = new Employee();
        manager.setId(managerId);
        return manager;
    }
}