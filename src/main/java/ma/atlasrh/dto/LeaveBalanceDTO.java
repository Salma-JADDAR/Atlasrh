package ma.atlasrh.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record LeaveBalanceDTO(

        @NotNull(message = "L'identifiant de l'employé est obligatoire.")
        @Positive(message = "L'identifiant de l'employé doit être positif.")
        Long employeeId,

        @NotNull(message = "L'identifiant du type de congé est obligatoire.")
        @Positive(message = "L'identifiant du type de congé doit être positif.")
        Long leaveTypeId,

        @NotBlank(message = "Le nom du type de congé est obligatoire.")
        @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères.")
        String leaveTypeName,

        @Min(value = 1, message = "L'année doit être supérieure ou égale à 1.")
        @Max(value = 9999, message = "L'année ne doit pas dépasser 9999.")
        int year,

        @PositiveOrZero(
                message = "Le quota annuel ne peut pas être négatif."
        )
        int annualAllowance,

        @PositiveOrZero(
                message = "Le nombre de jours utilisés ne peut pas être négatif."
        )
        int usedDays,

        @PositiveOrZero(
                message = "Le nombre de jours restants ne peut pas être négatif."
        )
        int remainingDays

) {
}