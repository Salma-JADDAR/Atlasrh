package ma.atlasrh.service;

import ma.atlasrh.entity.LeaveRequest;

import java.util.List;

public interface LeaveRequestService {

    LeaveRequest createLeaveRequest(LeaveRequest leaveRequest);

    LeaveRequest getLeaveRequestById(Long id);

    List<LeaveRequest> getRequestsByEmployee(Long employeeId);

    List<LeaveRequest> getPendingRequestsByManager(Long managerId);

    LeaveRequest approveLeaveRequest(Long id, Long managerId);

    LeaveRequest rejectLeaveRequest(
            Long id,
            Long managerId,
            String managerComment
    );

    LeaveRequest cancelLeaveRequest(Long id);
}