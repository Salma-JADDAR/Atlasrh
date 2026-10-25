package ma.atlasrh.service;

import ma.atlasrh.dto.LeaveBalanceDTO;

public interface LeaveBalanceService {

    LeaveBalanceDTO getLeaveBalance(
            Long employeeId,
            Long leaveTypeId,
            int year
    );

    int getUsedLeaveDays(
            Long employeeId,
            Long leaveTypeId,
            int year
    );

    int getRemainingLeaveDays(
            Long employeeId,
            Long leaveTypeId,
            int year
    );
}