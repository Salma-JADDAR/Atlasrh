package ma.atlasrh.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import ma.atlasrh.enums.ContractType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractDTO(

        Long id,

        @NotNull(message = "Le type de contrat est obligatoire.")
        ContractType contractType,

        @NotNull(message = "La date de début est obligatoire.")
        LocalDate startDate,

        LocalDate endDate,

        @NotNull(message = "Le salaire brut est obligatoire.")
        @DecimalMin(
                value = "0.01",
                message = "Le salaire brut doit être supérieur à zéro."
        )
        @Digits(
                integer = 8,
                fraction = 2,
                message = "Le salaire doit contenir au maximum 8 chiffres entiers et 2 décimales."
        )
        BigDecimal grossSalary,

        @NotNull(message = "L'employé associé est obligatoire.")
        @Positive(message = "L'identifiant de l'employé doit être positif.")
        Long employeeId

) {
}