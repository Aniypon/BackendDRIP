package org.example.dz7.controller;

import org.example.dz7.service.DecisionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DecisionController {

    private final DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @GetMapping("/decision")
    public Map<String, Object> decision(@RequestParam int amount, @RequestParam(defaultValue = "false") boolean vip) {
        return decisionService.evaluate(amount, vip);
    }

    @GetMapping("/healthcheck")
    public Map<String, String> healthcheck() {
        return Map.of("status", "ok");
    }
}
