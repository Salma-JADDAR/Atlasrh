package ma.atlasrh.mapper;

import ma.atlasrh.dto.LeaveRequestDTO;
import ma.atlasrh.entity.Employee;
import ma.atlasrh.entity.LeaveRequest;
import ma.atlasrh.entity.LeaveType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaveRequestMapper {

    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "leaveTypeId", source = "leaveType.id")
    LeaveRequestDTO toDto(LeaveRequest leaveRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(
            target = "employee",
            expression = "java(toEmployee(leaveRequestDTO.employeeId()))"
    )
    @Mapping(
            target = "leaveType",
            expression = "java(toLeaveType(leaveRequestDTO.leaveTypeId()))"
    )
    @Mapping(target = "workingDays", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "managerComment", ignore = true)
    LeaveRequest toEntity(LeaveRequestDTO leaveRequestDTO);

    default Employee toEmployee(Long employeeId) {
        if (employeeId == null) {
            return null;
        }

        Employee employee = new Employee();
        employee.setId(employeeId);
        return employee;
    }

    default LeaveType toLeaveType(Long leaveTypeId) {
        if (leaveTypeId == null) {
            return null;
        }

        LeaveType leaveType = new LeaveType();
        leaveType.setId(leaveTypeId);
        return leaveType;
    }
}