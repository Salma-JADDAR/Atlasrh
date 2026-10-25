package ma.atlasrh.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentDTO(

        Long id,

        @NotBlank(message = "Le nom du département est obligatoire.")
        @Size(
                max = 100,
                message = "Le nom du département ne doit pas dépasser 100 caractères."
        )
        String name,

        @Size(
                max = 255,
                message = "La description ne doit pas dépasser 255 caractères."
        )
        String description

) {
}