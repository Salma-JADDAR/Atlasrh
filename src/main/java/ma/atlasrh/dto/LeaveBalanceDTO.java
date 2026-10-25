package ma.atlasrh.dto;

public record LeaveBalanceDTO(
        Long employeeId,
        Long leaveTypeId,
        String leaveTypeName,
        int year,
        int annualAllowance,
        int usedDays,
        int remainingDays
) {
}