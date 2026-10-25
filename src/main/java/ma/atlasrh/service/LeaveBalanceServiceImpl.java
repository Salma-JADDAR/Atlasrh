
package ma.atlasrh.service;

import ma.atlasrh.dto.LeaveBalanceDTO;
import ma.atlasrh.entity.LeaveRequest;
import ma.atlasrh.entity.LeaveType;
import ma.atlasrh.enums.LeaveRequestStatus;
import ma.atlasrh.exception.BusinessRuleException;
import ma.atlasrh.exception.ResourceNotFoundException;
import ma.atlasrh.repository.EmployeeRepository;
import ma.atlasrh.repository.LeaveRequestRepository;
import ma.atlasrh.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveBalanceServiceImpl(
            EmployeeRepository employeeRepository,
            LeaveTypeRepository leaveTypeRepository,
            LeaveRequestRepository leaveRequestRepository) {
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveBalanceDTO getLeaveBalance(
            Long employeeId,
            Long leaveTypeId,
            int year) {

        validateParameters(employeeId, leaveTypeId, year);

        LeaveType leaveType = findLeaveTypeOrThrow(leaveTypeId);
        validateAnnualAllowance(leaveType);

        int usedDays = calculateUsedLeaveDays(
                employeeId,
                leaveTypeId,
                year
        );

        int annualAllowance = leaveType.getAnnualAllowance();
        int remainingDays = Math.max(0, annualAllowance - usedDays);

        return new LeaveBalanceDTO(
                employeeId,
                leaveTypeId,
                leaveType.getName(),
                year,
                annualAllowance,
                usedDays,
                remainingDays
        );
    }

    @Override
    @Transactional(readOnly = true)
    public int getUsedLeaveDays(
            Long employeeId,
            Long leaveTypeId,
            int year) {

        validateParameters(employeeId, leaveTypeId, year);
        findLeaveTypeOrThrow(leaveTypeId);

        return calculateUsedLeaveDays(employeeId, leaveTypeId, year);
    }

    @Override
    @Transactional(readOnly = true)
    public int getRemainingLeaveDays(
            Long employeeId,
            Long leaveTypeId,
            int year) {

        validateParameters(employeeId, leaveTypeId, year);

        LeaveType leaveType = findLeaveTypeOrThrow(leaveTypeId);
        validateAnnualAllowance(leaveType);

        int usedDays = calculateUsedLeaveDays(
                employeeId,
                leaveTypeId,
                year
        );

        return Math.max(0, leaveType.getAnnualAllowance() - usedDays);
    }

    private int calculateUsedLeaveDays(
            Long employeeId,
            Long leaveTypeId,
            int year) {

        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31);

        List<LeaveRequest> approvedRequests =
                leaveRequestRepository
                        .findByEmployee_IdAndLeaveType_IdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                employeeId,
                                leaveTypeId,
                                LeaveRequestStatus.APPROVED,
                                yearEnd,
                                yearStart
                        );

        int usedDays = 0;

        for (LeaveRequest request : approvedRequests) {
            LocalDate effectiveStart = request.getStartDate().isBefore(yearStart)
                    ? yearStart
                    : request.getStartDate();

            LocalDate effectiveEnd = request.getEndDate().isAfter(yearEnd)
                    ? yearEnd
                    : request.getEndDate();

            usedDays += calculateWorkingDays(
                    effectiveStart,
                    effectiveEnd
            );
        }

        return usedDays;
    }

    private int calculateWorkingDays(
            LocalDate startDate,
            LocalDate endDate) {

        int workingDays = 0;

        for (LocalDate date = startDate;
             !date.isAfter(endDate);
             date = date.plusDays(1)) {

            DayOfWeek day = date.getDayOfWeek();

            if (day != DayOfWeek.SATURDAY
                    && day != DayOfWeek.SUNDAY) {
                workingDays++;
            }
        }

        return workingDays;
    }

    private LeaveType findLeaveTypeOrThrow(Long leaveTypeId) {
        return leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Type de congé introuvable avec l'identifiant : "
                                + leaveTypeId
                ));
    }

    private void validateParameters(
            Long employeeId,
            Long leaveTypeId,
            int year) {

        if (employeeId == null || employeeId <= 0) {
            throw new BusinessRuleException(
                    "L'identifiant de l'employé doit être positif."
            );
        }

        if (leaveTypeId == null || leaveTypeId <= 0) {
            throw new BusinessRuleException(
                    "L'identifiant du type de congé doit être positif."
            );
        }

        if (year < 1 || year > 9999) {
            throw new BusinessRuleException(
                    "L'année doit être comprise entre 1 et 9999."
            );
        }

        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException(
                    "Employé introuvable avec l'identifiant : " + employeeId
            );
        }
    }

    private void validateAnnualAllowance(LeaveType leaveType) {
        if (leaveType.getAnnualAllowance() == null
                || leaveType.getAnnualAllowance() < 0) {
            throw new BusinessRuleException(
                    "Le quota annuel de ce type de congé est invalide."
            );
        }
    }
}
