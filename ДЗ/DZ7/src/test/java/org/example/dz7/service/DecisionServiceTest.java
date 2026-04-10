package org.example.dz7.service;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.tracing.test.simple.SimpleTracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DecisionServiceTest {

    private DecisionService service;
    private SimpleMeterRegistry registry;

    @BeforeEach
    void setUp() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.update(anyString(), (Object[]) any())).thenReturn(1);
        registry = new SimpleMeterRegistry();
        service = new DecisionService(jdbcTemplate, new SimpleTracer(), registry);
    }

    @Test
    void premiumBranchForLargeAmountAndVip() {
        Map<String, Object> result = service.evaluate(2000, true);
        assertEquals("premium", result.get("branch"));
        assertEquals("APPROVED_PREMIUM", result.get("decision"));
    }

    @Test
    void manualReviewForLargeAmountNonVip() {
        Map<String, Object> result = service.evaluate(2000, false);
        assertEquals("manual_review", result.get("branch"));
        assertEquals("REVIEW_REQUIRED", result.get("decision"));
    }

    @Test
    void autoApproveForMediumAmount() {
        Map<String, Object> result = service.evaluate(500, false);
        assertEquals("auto_approve", result.get("branch"));
        assertEquals("APPROVED_AUTO", result.get("decision"));
    }

    @Test
    void rejectForSmallAmount() {
        Map<String, Object> result = service.evaluate(50, false);
        assertEquals("reject", result.get("branch"));
        assertEquals("REJECTED", result.get("decision"));
    }

    @Test
    void incrementsBranchCounter() {
        service.evaluate(2000, true);
        double count = registry.counter("decision_branch_total", "branch", "premium").count();
        assertEquals(1.0, count);
    }
}
