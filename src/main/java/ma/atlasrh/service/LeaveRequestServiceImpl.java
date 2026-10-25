
package ma.atlasrh.service;

import ma.atlasrh.entity.Employee;
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
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveRequestServiceImpl(
            LeaveRequestRepository leaveRequestRepository,
            EmployeeRepository employeeRepository,
            LeaveTypeRepository leaveTypeRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    @Override
    @Transactional
    public LeaveRequest createLeaveRequest(LeaveRequest leaveRequest) {
        validateLeaveRequest(leaveRequest);

        Employee employee = findEmployeeForRequest(leaveRequest);
        LeaveType leaveType = findLeaveTypeForRequest(leaveRequest);

        List<LeaveRequestStatus> activeStatuses = List.of(
                LeaveRequestStatus.PENDING,
                LeaveRequestStatus.APPROVED
        );

        boolean overlappingRequest =
                leaveRequestRepository
                        .existsByEmployee_IdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                employee.getId(),
                                activeStatuses,
                                leaveRequest.getEndDate(),
                                leaveRequest.getStartDate()
                        );

        if (overlappingRequest) {
            throw new BusinessRuleException(
                    "La période demandée chevauche une autre demande "
                            + "de congé en cours ou déjà approuvée."
            );
        }

        int workingDays = calculateWorkingDays(
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate()
        );

        if (workingDays == 0) {
            throw new BusinessRuleException(
                    "La période doit contenir au moins un jour ouvrable."
            );
        }

        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setWorkingDays(workingDays);
        leaveRequest.setStatus(LeaveRequestStatus.PENDING);
        leaveRequest.setManagerComment(null);

        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveRequest getLeaveRequestById(Long id) {
        validateId(id);
        return findLeaveRequestOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> getRequestsByEmployee(Long employeeId) {
        validateId(employeeId);

        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException(
                    "Employé introuvable avec l'identifiant : " + employeeId
            );
        }

        return leaveRequestRepository.findByEmployee_Id(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> getPendingRequests() {
        return leaveRequestRepository.findByStatus(
                LeaveRequestStatus.PENDING
        );
    }

    @Override
    @Transactional
    public LeaveRequest approveLeaveRequest(Long id) {
        validateId(id);

        LeaveRequest leaveRequest = findLeaveRequestOrThrow(id);
        validatePendingStatus(leaveRequest);

        leaveRequest.setStatus(LeaveRequestStatus.APPROVED);

        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    @Transactional
    public LeaveRequest rejectLeaveRequest(
            Long id,
            String managerComment) {
        validateId(id);

        LeaveRequest leaveRequest = findLeaveRequestOrThrow(id);
        validatePendingStatus(leaveRequest);

        leaveRequest.setStatus(LeaveRequestStatus.REJECTED);

        if (managerComment == null || managerComment.isBlank()) {
            leaveRequest.setManagerComment(null);
        } else {
            leaveRequest.setManagerComment(managerComment.trim());
        }

        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    @Transactional
    public LeaveRequest cancelLeaveRequest(Long id) {
        validateId(id);

        LeaveRequest leaveRequest = findLeaveRequestOrThrow(id);
        LeaveRequestStatus status = leaveRequest.getStatus();

        if (status != LeaveRequestStatus.PENDING
                && status != LeaveRequestStatus.APPROVED) {
            throw new BusinessRuleException(
                    "Seules les demandes en attente ou approuvées "
                            + "peuvent être annulées."
            );
        }

        if (status == LeaveRequestStatus.APPROVED
                && !leaveRequest.getStartDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException(
                    "Une demande approuvée ne peut plus être annulée "
                            + "à partir du jour de début du congé."
            );
        }

        leaveRequest.setStatus(LeaveRequestStatus.CANCELLED);

        return leaveRequestRepository.save(leaveRequest);
    }

    private LeaveRequest findLeaveRequestOrThrow(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Demande de congé introuvable avec l'identifiant : "
                                + id
                ));
    }

    private Employee findEmployeeForRequest(LeaveRequest leaveRequest) {
        if (leaveRequest.getEmployee() == null
                || leaveRequest.getEmployee().getId() == null) {
            throw new BusinessRuleException(
                    "L'employé est obligatoire pour créer une demande."
            );
        }

        Long employeeId = leaveRequest.getEmployee().getId();

        if (employeeId <= 0) {
            throw new BusinessRuleException(
                    "L'identifiant de l'employé doit être positif."
            );
        }

        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employé introuvable avec l'identifiant : "
                                + employeeId
                ));
    }

    private LeaveType findLeaveTypeForRequest(
            LeaveRequest leaveRequest) {
        if (leaveRequest.getLeaveType() == null
                || leaveRequest.getLeaveType().getId() == null) {
            throw new BusinessRuleException(
                    "Le type de congé est obligatoire."
            );
        }

        Long leaveTypeId = leaveRequest.getLeaveType().getId();

        if (leaveTypeId <= 0) {
            throw new BusinessRuleException(
                    "L'identifiant du type de congé doit être positif."
            );
        }

        return leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Type de congé introuvable avec l'identifiant : "
                                + leaveTypeId
                ));
    }

    private void validateLeaveRequest(LeaveRequest leaveRequest) {
        if (leaveRequest == null) {
            throw new BusinessRuleException(
                    "Les informations de la demande sont obligatoires."
            );
        }

        if (leaveRequest.getStartDate() == null
                || leaveRequest.getEndDate() == null) {
            throw new BusinessRuleException(
                    "Les dates de début et de fin sont obligatoires."
            );
        }

        if (leaveRequest.getEndDate()
                .isBefore(leaveRequest.getStartDate())) {
            throw new BusinessRuleException(
                    "La date de fin ne peut pas précéder la date de début."
            );
        }

        if (leaveRequest.getReason() == null
                || leaveRequest.getReason().isBlank()) {
            throw new BusinessRuleException(
                    "Le motif de la demande est obligatoire."
            );
        }
    }

    private void validatePendingStatus(LeaveRequest leaveRequest) {
        if (leaveRequest.getStatus() != LeaveRequestStatus.PENDING) {
            throw new BusinessRuleException(
                    "Seules les demandes en attente peuvent être approuvées "
                            + "ou rejetées."
            );
        }
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

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessRuleException(
                    "L'identifiant doit être un nombre positif."
            );
        }
    }
}
