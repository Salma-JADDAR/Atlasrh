
package ma.atlasrh.controller;

import jakarta.validation.Valid;
import ma.atlasrh.dto.LeaveRequestDTO;
import ma.atlasrh.entity.LeaveRequest;
import ma.atlasrh.mapper.LeaveRequestMapper;
import ma.atlasrh.service.LeaveRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;
    private final LeaveRequestMapper leaveRequestMapper;

    public LeaveRequestController(
            LeaveRequestService leaveRequestService,
            LeaveRequestMapper leaveRequestMapper) {
        this.leaveRequestService = leaveRequestService;
        this.leaveRequestMapper = leaveRequestMapper;
    }

    @PostMapping
    public ResponseEntity<LeaveRequestDTO> createLeaveRequest(
            @Valid @RequestBody LeaveRequestDTO leaveRequestDTO) {

        LeaveRequest leaveRequest =
                leaveRequestMapper.toEntity(leaveRequestDTO);

        LeaveRequest createdRequest =
                leaveRequestService.createLeaveRequest(leaveRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(leaveRequestMapper.toDto(createdRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> getLeaveRequestById(
            @PathVariable Long id) {

        LeaveRequest leaveRequest =
                leaveRequestService.getLeaveRequestById(id);

        return ResponseEntity.ok(
                leaveRequestMapper.toDto(leaveRequest)
        );
    }

    @GetMapping("/by-employee/{employeeId}")
    public ResponseEntity<List<LeaveRequestDTO>> getRequestsByEmployee(
            @PathVariable Long employeeId) {

        List<LeaveRequestDTO> requests =
                leaveRequestService.getRequestsByEmployee(employeeId)
                        .stream()
                        .map(leaveRequestMapper::toDto)
                        .toList();

        return ResponseEntity.ok(requests);
    }

    @GetMapping("/by-manager/{managerId}/pending")
    public ResponseEntity<List<LeaveRequestDTO>> getPendingRequestsByManager(
            @PathVariable Long managerId) {

        List<LeaveRequestDTO> requests =
                leaveRequestService.getPendingRequestsByManager(managerId)
                        .stream()
                        .map(leaveRequestMapper::toDto)
                        .toList();

        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<LeaveRequestDTO> approveLeaveRequest(
            @PathVariable Long id,
            @RequestParam Long managerId) {

        LeaveRequest approvedRequest =
                leaveRequestService.approveLeaveRequest(id, managerId);

        return ResponseEntity.ok(
                leaveRequestMapper.toDto(approvedRequest)
        );
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<LeaveRequestDTO> rejectLeaveRequest(
            @PathVariable Long id,
            @RequestParam Long managerId,
            @RequestParam(required = false) String managerComment) {

        LeaveRequest rejectedRequest =
                leaveRequestService.rejectLeaveRequest(
                        id,
                        managerId,
                        managerComment
                );

        return ResponseEntity.ok(
                leaveRequestMapper.toDto(rejectedRequest)
        );
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<LeaveRequestDTO> cancelLeaveRequest(
            @PathVariable Long id) {

        LeaveRequest cancelledRequest =
                leaveRequestService.cancelLeaveRequest(id);

        return ResponseEntity.ok(
                leaveRequestMapper.toDto(cancelledRequest)
        );
    }
}
