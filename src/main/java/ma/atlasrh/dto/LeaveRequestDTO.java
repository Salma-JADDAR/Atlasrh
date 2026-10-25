package ma.atlasrh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import ma.atlasrh.enums.LeaveRequestStatus;

import java.time.LocalDate;

public record LeaveRequestDTO(

        Long id,

        @NotNull(message = "La date de début est obligatoire.")
        LocalDate startDate,

        @NotNull(message = "La date de fin est obligatoire.")
        LocalDate endDate,

        @NotBlank(message = "Le motif de la demande est obligatoire.")
        @Size(max = 500, message = "Le motif ne doit pas dépasser 500 caractères.")
        String reason,

        Integer workingDays,

        LeaveRequestStatus status,

        @Size(max = 500, message = "Le commentaire ne doit pas dépasser 500 caractères.")
        String managerComment,

        @NotNull(message = "L'employé est obligatoire.")
        @Positive(message = "L'identifiant de l'employé doit être positif.")
        Long employeeId,

        @NotNull(message = "Le type de congé est obligatoire.")
        @Positive(message = "L'identifiant du type de congé doit être positif.")
        Long leaveTypeId

) {
}