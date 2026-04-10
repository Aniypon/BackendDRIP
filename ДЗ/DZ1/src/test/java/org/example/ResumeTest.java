package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumeTest {

    private final Resume resume = new Resume();

    @Test
    void shortSummaryContainsNameAndRole() {
        String summary = resume.shortSummary();

        assertNotNull(summary);
        assertTrue(summary.contains("Арсений Пономарев"));
        assertTrue(summary.contains("Java Backend"));
    }

    @Test
    void shortSummaryIsSingleLine() {
        assertTrue(resume.shortSummary().lines().count() == 1);
    }
}
