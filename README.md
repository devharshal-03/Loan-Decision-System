# Loan Decision System

A Spring Boot REST service that evaluates a loan application and returns either a single loan offer (approved) or the reasons for rejection. Every decision is stored in memory for audit.

**Tech:** Java 21, Spring Boot 4.2, Maven

## Run and test

    .\mvnw.cmd spring-boot:run     # starts on http://localhost:8080
    .\mvnw.cmd clean test          # runs all tests

(On Linux/macOS use `./mvnw` instead of `.\mvnw.cmd`.)

## API

`POST /applications`

Request:

    {
      "applicant": {
        "name": "Test",
        "age": 30,
        "monthlyIncome": 75000,
        "employmentType": "SALARIED",
        "creditScore": 720
      },
      "loan": {
        "amount": 500000,
        "tenureMonths": 36,
        "purpose": "PERSONAL"
      }
    }

Approved (HTTP 201):

    {
      "applicationId": "dfc680ff-d29c-4387-aa7e-88763cbd0137",
      "status": "APPROVED",
      "riskBand": "MEDIUM",
      "offer": { "interestRate": 13.5, "tenureMonths": 36, "emi": 16967.64, "totalPayable": 610835.04 }
    }

Rejected (HTTP 201):

    {
      "applicationId": "...",
      "status": "REJECTED",
      "riskBand": null,
      "rejectionReasons": ["CREDIT_SCORE_BELOW_MINIMUM"]
    }

Invalid input (HTTP 400):

    { "errors": ["Age must be between 21 and 60"] }

## Business rules

- **Validation:** age 21-60, credit score 300-900, amount 10,000-50,00,000, tenure 6-360 months, income greater than 0.
- **Rejection:** credit score below 600, age + tenure above 65 years, EMI above 60% of income, or EMI above 50% of income (no offer is made). All failed rules are listed.
- **Risk band:** 750+ LOW, 650-749 MEDIUM, 600-649 HIGH.
- **Interest rate:** 12% base + risk (0 / 1.5 / 3) + self-employed (1) + loan above 10,00,000 (0.5).
- **EMI:** `P * r * (1+r)^n / ((1+r)^n - 1)` with `BigDecimal`, scale 2, `HALF_UP`.

Rejection reasons: `CREDIT_SCORE_BELOW_MINIMUM`, `AGE_TENURE_LIMIT_EXCEEDED`, `EMI_EXCEEDS_60_PERCENT`, `EMI_EXCEEDS_50_PERCENT`.

## Structure

    controller   REST endpoint
    service      business rules and decision flow
    domain       enums and decision/offer models
    dto          request, response and error models
    repository   in-memory decision store
    exception    HTTP 400 error handling

See [DEVELOPMENT_NOTES.md](DEVELOPMENT_NOTES.md) for design decisions, assumptions and trade-offs.
