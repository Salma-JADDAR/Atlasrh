package ma.atlasrh.mapper;

import ma.atlasrh.dto.LeaveTypeDTO;
import ma.atlasrh.entity.LeaveType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaveTypeMapper {

    LeaveTypeDTO toDto(LeaveType leaveType);

    @Mapping(target = "id", ignore = true)
    LeaveType toEntity(LeaveTypeDTO leaveTypeDTO);
}