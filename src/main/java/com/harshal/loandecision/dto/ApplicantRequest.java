package com.harshal.loandecision.dto;

import com.harshal.loandecision.domain.EmploymentType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ApplicantRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Age is required")
        @Min(value = 21, message = "Age must be between 21 and 60")
        @Max(value = 60, message = "Age must be between 21 and 60")
        Integer age,

        @NotNull(message = "Monthly income is required")
        @DecimalMin(value = "0", inclusive = false, message = "Monthly income must be greater than 0")
        BigDecimal monthlyIncome,

        @NotNull(message = "Employment type is required (SALARIED or SELF_EMPLOYED)")
        EmploymentType employmentType,

        @NotNull(message = "Credit score is required")
        @Min(value = 300, message = "Credit score must be between 300 and 900")
        @Max(value = 900, message = "Credit score must be between 300 and 900")
        Integer creditScore
) {
}