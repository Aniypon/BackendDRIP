package org.example.dz7.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DecisionService {

    private static final Logger log = LoggerFactory.getLogger(DecisionService.class);

    private final JdbcTemplate jdbcTemplate;
    private final Tracer tracer;

    private final Counter premiumCounter;
    private final Counter manualCounter;
    private final Counter autoCounter;
    private final Counter rejectCounter;
    private final Counter errorCounter;

    private final Timer premiumTimer;
    private final Timer manualTimer;
    private final Timer autoTimer;
    private final Timer rejectTimer;

    public DecisionService(JdbcTemplate jdbcTemplate, Tracer tracer, MeterRegistry registry) {
        this.jdbcTemplate = jdbcTemplate;
        this.tracer = tracer;

        this.premiumCounter = registry.counter("decision_branch_total", "branch", "premium");
        this.manualCounter = registry.counter("decision_branch_total", "branch", "manual_review");
        this.autoCounter = registry.counter("decision_branch_total", "branch", "auto_approve");
        this.rejectCounter = registry.counter("decision_branch_total", "branch", "reject");
        this.errorCounter = registry.counter("decision_errors_total");

        this.premiumTimer = Timer.builder("decision_branch_latency_seconds").tag("branch", "premium").register(registry);
        this.manualTimer = Timer.builder("decision_branch_latency_seconds").tag("branch", "manual_review").register(registry);
        this.autoTimer = Timer.builder("decision_branch_latency_seconds").tag("branch", "auto_approve").register(registry);
        this.rejectTimer = Timer.builder("decision_branch_latency_seconds").tag("branch", "reject").register(registry);
    }

    public Map<String, Object> evaluate(int amount, boolean vip) {
        Span rootSpan = tracer.nextSpan().name("decision.evaluate").start();
        long startedAt = System.nanoTime();
        try (Tracer.SpanInScope ws = tracer.withSpan(rootSpan)) {
            rootSpan.tag("amount", String.valueOf(amount));
            rootSpan.tag("vip", String.valueOf(vip));
            log.info("Decision request received amount={} vip={}", amount, vip);

            if (amount >= 1000 && vip) {
                return processBranch("premium", "APPROVED_PREMIUM", premiumCounter, premiumTimer, amount, vip);
            } else if (amount >= 1000) {
                return processBranch("manual_review", "REVIEW_REQUIRED", manualCounter, manualTimer, amount, vip);
            } else if (amount >= 100) {
                return processBranch("auto_approve", "APPROVED_AUTO", autoCounter, autoTimer, amount, vip);
            } else {
                return processBranch("reject", "REJECTED", rejectCounter, rejectTimer, amount, vip);
            }
        } catch (Exception e) {
            errorCounter.increment();
            rootSpan.error(e);
            log.error("Decision processing failed", e);
            throw e;
        } finally {
            rootSpan.tag("elapsed_ms", String.valueOf((System.nanoTime() - startedAt) / 1_000_000));
            rootSpan.end();
        }
    }

    private Map<String, Object> processBranch(String branch,
                                              String decision,
                                              Counter counter,
                                              Timer timer,
                                              int amount,
                                              boolean vip) {
        return timer.record(() -> {
            Span branchSpan = tracer.nextSpan().name("decision.branch." + branch).start();
            try (Tracer.SpanInScope ws = tracer.withSpan(branchSpan)) {
                branchSpan.tag("branch", branch);
                branchSpan.tag("decision", decision);
                log.info("Branch={} decision={} amount={} vip={}", branch, decision, amount, vip);

                counter.increment();
                jdbcTemplate.update(
                        "INSERT INTO decision_audit(amount, vip, branch, decision) VALUES (?,?,?,?)",
                        amount,
                        vip,
                        branch,
                        decision
                );

                return Map.of(
                        "branch", branch,
                        "decision", decision,
                        "amount", amount,
                        "vip", vip
                );
            } catch (Exception e) {
                branchSpan.error(e);
                throw e;
            } finally {
                branchSpan.end();
            }
        });
    }
}
