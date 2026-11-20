package com.harshal.loandecision.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoanApplicationValidationTest {

    private static final String AGE_MESSAGE = "Age must be between 21 and 60";
    private static final String CREDIT_SCORE_MESSAGE = "Credit score must be between 300 and 900";
    private static final String AMOUNT_MESSAGE = "Loan amount must be between 10,000 and 50,00,000";
    private static final String TENURE_MESSAGE = "Tenure must be between 6 and 360 months";
    private static final String INCOME_MESSAGE = "Monthly income must be greater than 0";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void ageBelow21IsRejected() throws Exception {
        expectBadRequest(requestJson(20, 75000, 720, 500000, 36), AGE_MESSAGE);
    }

    @Test
    void ageAbove60IsRejected() throws Exception {
        expectBadRequest(requestJson(61, 75000, 720, 500000, 36), AGE_MESSAGE);
    }

    @Test
    void creditScoreBelow300IsRejected() throws Exception {
        expectBadRequest(requestJson(30, 75000, 299, 500000, 36), CREDIT_SCORE_MESSAGE);
    }

    @Test
    void creditScoreAbove900IsRejected() throws Exception {
        expectBadRequest(requestJson(30, 75000, 901, 500000, 36), CREDIT_SCORE_MESSAGE);
    }

    @Test
    void loanAmountBelow10000IsRejected() throws Exception {
        expectBadRequest(requestJson(30, 75000, 720, 9999, 36), AMOUNT_MESSAGE);
    }

    @Test
    void loanAmountAbove50LakhIsRejected() throws Exception {
        expectBadRequest(requestJson(30, 75000, 720, 5000001, 36), AMOUNT_MESSAGE);
    }

    @Test
    void tenureBelow6MonthsIsRejected() throws Exception {
        expectBadRequest(requestJson(30, 75000, 720, 500000, 5), TENURE_MESSAGE);
    }

    @Test
    void tenureAbove360MonthsIsRejected() throws Exception {
        expectBadRequest(requestJson(30, 75000, 720, 500000, 361), TENURE_MESSAGE);
    }

    @Test
    void monthlyIncomeOfZeroIsRejected() throws Exception {
        expectBadRequest(requestJson(30, 0, 720, 500000, 36), INCOME_MESSAGE);
    }

    @Test
    void valuesOnTheBoundariesAreAccepted() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(21, 75000, 300, 10000, 6)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson(60, 75000, 900, 5000000, 360)))
                .andExpect(status().isCreated());
    }

    @Test
    void allViolatedRulesAreReportedTogether() throws Exception {
        expectBadRequest(requestJson(18, 75000, 100, 500000, 36), AGE_MESSAGE, CREDIT_SCORE_MESSAGE);
    }

    @Test
    void missingApplicantIsRejected() throws Exception {
        String json = """
                {
                  "loan": {
                    "amount": 500000,
                    "tenureMonths": 36,
                    "purpose": "PERSONAL"
                  }
                }
                """;

        expectBadRequest(json, "Applicant details are required");
    }

    @Test
    void invalidEmploymentTypeIsRejected() throws Exception {
        String json = requestJson(30, 75000, 720, 500000, 36).replace("SALARIED", "CONTRACTOR");

        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value(containsString("SALARIED or SELF_EMPLOYED")));
    }

    @Test
    void malformedJsonIsRejected() throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ not valid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value(containsString("malformed")));
    }

    private void expectBadRequest(String json, String... messages) throws Exception {
        mockMvc.perform(post("/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItems(messages)));
    }

    private String requestJson(int age, int monthlyIncome, int creditScore, int amount, int tenureMonths) {
        return """
                {
                  "applicant": {
                    "name": "Test Applicant",
                    "age": %d,
                    "monthlyIncome": %d,
                    "employmentType": "SALARIED",
                    "creditScore": %d
                  },
                  "loan": {
                    "amount": %d,
                    "tenureMonths": %d,
                    "purpose": "PERSONAL"
                  }
                }
                """.formatted(age, monthlyIncome, creditScore, amount, tenureMonths);
    }
}