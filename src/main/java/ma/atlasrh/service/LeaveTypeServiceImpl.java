
package ma.atlasrh.service;

import ma.atlasrh.entity.LeaveType;
import ma.atlasrh.exception.BusinessRuleException;
import ma.atlasrh.exception.DuplicateResourceException;
import ma.atlasrh.exception.ResourceNotFoundException;
import ma.atlasrh.repository.LeaveRequestRepository;
import ma.atlasrh.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveTypeServiceImpl(
            LeaveTypeRepository leaveTypeRepository,
            LeaveRequestRepository leaveRequestRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Override
    @Transactional
    public LeaveType createLeaveType(LeaveType leaveType) {
        validateLeaveType(leaveType);

        String name = leaveType.getName().trim();

        if (leaveTypeRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException(
                    "Un type de congé avec ce nom existe déjà."
            );
        }

        leaveType.setName(name);
        normalizeDescription(leaveType);

        return leaveTypeRepository.save(leaveType);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveType getLeaveTypeById(Long id) {
        validateId(id);

        return findLeaveTypeOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeRepository.findAll();
    }

    @Override
    @Transactional
    public LeaveType updateLeaveType(Long id, LeaveType leaveType) {
        validateId(id);
        validateLeaveType(leaveType);

        LeaveType existingLeaveType = findLeaveTypeOrThrow(id);
        String name = leaveType.getName().trim();

        if (leaveTypeRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new DuplicateResourceException(
                    "Un type de congé avec ce nom existe déjà."
            );
        }

        existingLeaveType.setName(name);
        existingLeaveType.setDescription(leaveType.getDescription());
        existingLeaveType.setAnnualAllowance(
                leaveType.getAnnualAllowance()
        );
        existingLeaveType.setPaid(leaveType.getPaid());

        normalizeDescription(existingLeaveType);

        return leaveTypeRepository.save(existingLeaveType);
    }

    @Override
    @Transactional
    public void deleteLeaveType(Long id) {
        validateId(id);

        LeaveType leaveType = findLeaveTypeOrThrow(id);

        if (leaveRequestRepository.existsByLeaveType_Id(id)) {
            throw new BusinessRuleException(
                    "Impossible de supprimer ce type de congé, "
                            + "car il est associé à des demandes de congé."
            );
        }

        leaveTypeRepository.delete(leaveType);
    }

    private LeaveType findLeaveTypeOrThrow(Long id) {
        return leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Type de congé introuvable avec l'identifiant : " + id
                ));
    }

    private void validateLeaveType(LeaveType leaveType) {
        if (leaveType == null) {
            throw new BusinessRuleException(
                    "Les informations du type de congé sont obligatoires."
            );
        }

        if (leaveType.getName() == null
                || leaveType.getName().isBlank()) {
            throw new BusinessRuleException(
                    "Le nom du type de congé est obligatoire."
            );
        }

        if (leaveType.getAnnualAllowance() == null
                || leaveType.getAnnualAllowance() < 0) {
            throw new BusinessRuleException(
                    "Le nombre de jours autorisés doit être positif ou nul."
            );
        }

        if (leaveType.getPaid() == null) {
            throw new BusinessRuleException(
                    "Le caractère payé ou non payé du congé doit être précisé."
            );
        }
    }

    private void normalizeDescription(LeaveType leaveType) {
        if (leaveType.getDescription() != null) {
            leaveType.setDescription(
                    leaveType.getDescription().trim()
            );
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessRuleException(
                    "L'identifiant doit être un nombre positif."
            );
        }
    }
}
