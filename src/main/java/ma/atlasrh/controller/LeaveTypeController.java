
package ma.atlasrh.controller;

import jakarta.validation.Valid;
import ma.atlasrh.dto.LeaveTypeDTO;
import ma.atlasrh.entity.LeaveType;
import ma.atlasrh.mapper.LeaveTypeMapper;
import ma.atlasrh.service.LeaveTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-types")
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;
    private final LeaveTypeMapper leaveTypeMapper;

    public LeaveTypeController(
            LeaveTypeService leaveTypeService,
            LeaveTypeMapper leaveTypeMapper) {
        this.leaveTypeService = leaveTypeService;
        this.leaveTypeMapper = leaveTypeMapper;
    }

    @PostMapping
    public ResponseEntity<LeaveTypeDTO> createLeaveType(
            @Valid @RequestBody LeaveTypeDTO leaveTypeDTO) {

        LeaveType leaveType = leaveTypeMapper.toEntity(leaveTypeDTO);
        LeaveType createdLeaveType =
                leaveTypeService.createLeaveType(leaveType);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(leaveTypeMapper.toDto(createdLeaveType));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveTypeDTO> getLeaveTypeById(
            @PathVariable Long id) {

        LeaveType leaveType = leaveTypeService.getLeaveTypeById(id);

        return ResponseEntity.ok(leaveTypeMapper.toDto(leaveType));
    }

    @GetMapping
    public ResponseEntity<List<LeaveTypeDTO>> getAllLeaveTypes() {

        List<LeaveTypeDTO> leaveTypes =
                leaveTypeService.getAllLeaveTypes()
                        .stream()
                        .map(leaveTypeMapper::toDto)
                        .toList();

        return ResponseEntity.ok(leaveTypes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveTypeDTO> updateLeaveType(
            @PathVariable Long id,
            @Valid @RequestBody LeaveTypeDTO leaveTypeDTO) {

        LeaveType leaveType = leaveTypeMapper.toEntity(leaveTypeDTO);
        LeaveType updatedLeaveType =
                leaveTypeService.updateLeaveType(id, leaveType);

        return ResponseEntity.ok(leaveTypeMapper.toDto(updatedLeaveType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveType(
            @PathVariable Long id) {

        leaveTypeService.deleteLeaveType(id);

        return ResponseEntity.noContent().build();
    }
}
