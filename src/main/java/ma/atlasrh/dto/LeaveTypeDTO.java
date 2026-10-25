package ma.atlasrh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record LeaveTypeDTO(

        Long id,

        @NotBlank(message = "Le nom du type de congé est obligatoire.")
        @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères.")
        String name,

        @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères.")
        String description,

        @NotNull(message = "Le nombre de jours autorisés est obligatoire.")
        @PositiveOrZero(message = "Le nombre de jours autorisés ne peut pas être négatif.")
        Integer annualAllowance,

        @NotNull(message = "Le caractère payé ou non payé doit être précisé.")
        Boolean paid

) {
}