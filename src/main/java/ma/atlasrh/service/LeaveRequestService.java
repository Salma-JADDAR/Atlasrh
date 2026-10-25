package ma.atlasrh.service;

import ma.atlasrh.entity.LeaveRequest;

import java.util.List;

public interface LeaveRequestService {

    LeaveRequest createLeaveRequest(LeaveRequest leaveRequest);

    LeaveRequest getLeaveRequestById(Long id);

    List<LeaveRequest> getRequestsByEmployee(Long employeeId);

    List<LeaveRequest> getPendingRequests();

    LeaveRequest approveLeaveRequest(Long id);

    LeaveRequest rejectLeaveRequest(Long id, String managerComment);

    LeaveRequest cancelLeaveRequest(Long id);
}