package ma.atlasrh.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EmployeeDTO(

        Long id,

        @NotBlank(message = "Le matricule est obligatoire.")
        @Size(max = 50, message = "Le matricule ne doit pas dépasser 50 caractères.")
        String matricule,

        @NotBlank(message = "Le prénom est obligatoire.")
        @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères.")
        String firstName,

        @NotBlank(message = "Le nom est obligatoire.")
        @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères.")
        String lastName,

        @NotBlank(message = "L'email est obligatoire.")
        @Email(message = "L'adresse email est invalide.")
        @Size(max = 150, message = "L'email ne doit pas dépasser 150 caractères.")
        String email,

        @Size(max = 100, message = "Le poste ne doit pas dépasser 100 caractères.")
        String position,

        @NotNull(message = "La date d'embauche est obligatoire.")
        LocalDate hireDate,

        @NotNull(message = "Le département est obligatoire.")
        @Positive(message = "L'identifiant du département doit être positif.")
        Long departmentId,

        @Positive(message = "L'identifiant du responsable doit être positif.")
        Long managerId

) {
}