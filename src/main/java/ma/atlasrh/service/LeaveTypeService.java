package ma.atlasrh.service;

import ma.atlasrh.entity.LeaveType;

import java.util.List;

public interface LeaveTypeService {

    LeaveType createLeaveType(LeaveType leaveType);

    LeaveType getLeaveTypeById(Long id);

    List<LeaveType> getAllLeaveTypes();

    LeaveType updateLeaveType(Long id, LeaveType leaveType);

    void deleteLeaveType(Long id);
}