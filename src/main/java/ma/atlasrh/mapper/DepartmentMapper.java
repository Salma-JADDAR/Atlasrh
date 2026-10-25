package ma.atlasrh.mapper;

import ma.atlasrh.dto.DepartmentDTO;
import ma.atlasrh.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentDTO toDto(Department department);

    @Mapping(target = "id", ignore = true)
    Department toEntity(DepartmentDTO departmentDTO);
}