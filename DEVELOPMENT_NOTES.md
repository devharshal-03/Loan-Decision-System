# Development Notes

## Overall approach

The service is a layered Spring Boot application: controller, service, domain and repository, with separate request and response DTOs. I built it in small steps, each with its own tests and commit: enums and request models, risk band, EMI, interest rate, eligibility, decision model, decision service, repository, controller and finally exception handling.

The business rules live in small, single-purpose service classes (`RiskBandClassifier`, `EmiCalculator`, `InterestRateCalculator`, `EligibilityEvaluator`). `LoanDecisionService` only orchestrates them, so each rule can be unit tested without Spring.

## Key design decisions

- **BigDecimal for all money and rates.** EMI intermediate steps use `MathContext.DECIMAL128` and the result is rounded once, at the end, to scale 2 with `RoundingMode.HALF_UP`.
- **Validation with Bean Validation annotations** on the request records, with the messages written for the API consumer. A `@RestControllerAdvice` turns validation failures and unreadable request bodies into HTTP 400 with an `errors` list.
- **Domain separate from DTOs.** `LoanDecision` and `LoanOffer` are domain records. `LoanApplicationResponse` shapes the JSON exactly as in the assignment: `riskBand` is always present (null on rejection), `offer` appears only when approved and `rejectionReasons` only when rejected.
- **Every decision is stored**, approved or rejected, in an in-memory repository.
- **Plain Java records, no Lombok**, to keep the code simple and the build small.
- **A business rejection is not a client error**, so a rejected application returns HTTP 201 (an application was created and stored) with status `REJECTED`. Only invalid input returns 400.

## Assumptions

1. **All failed rules are reported.** The sample rejection lists two reasons, so rules are not short-circuited.
2. **Age + tenure rule** is evaluated in months (`age * 12 + tenureMonths > 780`), which equals "age + tenure in years > 65" without rounding issues.
3. **Two EMI rejection codes.** EMI above 60% of income gives `EMI_EXCEEDS_60_PERCENT`. EMI above 50% but not above 60% gives `EMI_EXCEEDS_50_PERCENT` (the section 6 offer rule). An EMI above 60% gets only the 60% code, since it implies the 50% failure.
4. **EMI rules are checked only when the credit score is 600 or above.** The interest rate depends on the risk band, and scores below 600 have no band, so no rate or EMI can be derived. Such an application is rejected for the score (plus the age/tenure reason, if it applies).
5. **`riskBand` is null on every rejection**, following the sample rejected response.
6. **Total payable = EMI x tenure months**, using the EMI already rounded to 2 decimals.
7. **EMI is compared to the income thresholds after rounding** to 2 decimals.
8. **Loan size premium** applies only when the amount is strictly greater than 10,00,000.
9. **Validation ranges are inclusive** (for example age 21 and 60 are both valid).
10. **Applicant name must not be blank.** The assignment gives no rule for the name, so this is a minimal sanity check.
11. **The sample numbers in the assignment are illustrative.** By the given formula, 5,00,000 at 13.5% over 36 months gives an EMI of 16,967.64, not 16,234.23. The sample total of 584,432.23 is also not equal to 16,234.23 x 36. The implementation follows the formula, and the tests use independently calculated values.
12. **The interest rate** is returned as a percentage number (for example 13.5).

## Trade-offs considered

- **In-memory repository vs a database.** The assignment asks for a repository layer to store decisions but has no read endpoint or persistence requirement. A `ConcurrentHashMap` keeps the project small and dependency-free, at the cost of losing data on restart.
- **Nullable fields on `LoanDecision` and the response** instead of `Optional` or separate approved and rejected types. This keeps one simple response shape that matches the two JSON examples.
- **Throwing from `RiskBandClassifier` for scores below 600** rather than returning null. The decision service checks eligibility first, so the exception is a safeguard against misuse, not a normal path.
- **API tests run the real application through MockMvc** (real beans, no mocks), so they verify the actual response format and validation messages. They cost more startup time than unit tests.
- **The Maven wrapper is pinned to 3.9.11.** The default 3.10.1 failed to start on my machine.

## Testing

- Unit tests for the three areas the assignment names: EMI calculation, risk classification and eligibility logic, including boundary values (scores 599/600, age + tenure of exactly 65 years, EMI at exactly 50% and 60% of income).
- Unit tests for interest rate premiums, the decision service (approved, rejected, multiple reasons, decisions stored) and the repository.
- API tests for `POST /applications`: approved and rejected response formats, every validation rule from the assignment, accepted boundary values, missing applicant, invalid enum value and malformed JSON.

Run all tests with `.\mvnw.cmd clean test` and start the service with `.\mvnw.cmd spring-boot:run`.

## Refactoring done

- When the repository was introduced, `LoanDecisionService.evaluate` was split into a small public method that stores the decision and a private `decide` method that holds the rules, so storing is not mixed into the decision logic.
- The Maven wrapper was pinned in its own commit after the build environment problem described above.

## Improvements with more time

- Persist decisions in a database, and store the submitted application and a timestamp with each decision for a fuller audit trail.
- Add a read endpoint to fetch a decision by application id.
- Move rates, premiums and thresholds into configuration instead of constants.
- Include field names and error codes in 400 responses.
- Add OpenAPI documentation and request logging.