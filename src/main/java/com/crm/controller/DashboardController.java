package com.crm.controller;

import com.crm.entity.Lead;
import com.crm.repository.ActivityRepository;
import com.crm.repository.ContactRepository;
import com.crm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final LeadRepository leadRepository;
    private final ContactRepository contactRepository;
    private final ActivityRepository activityRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // Lead counts
        stats.put("totalLeads", leadRepository.countByStage(Lead.Stage.NEW) +
            leadRepository.countByStage(Lead.Stage.QUALIFIED) +
            leadRepository.countByStage(Lead.Stage.PROPOSITION));
        stats.put("wonLeads", leadRepository.countByStage(Lead.Stage.WON));
        stats.put("lostLeads", leadRepository.countByStage(Lead.Stage.LOST));

        // Revenue
        BigDecimal totalRevenue = leadRepository.totalWonRevenue();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        BigDecimal expectedRevenue = leadRepository.sumExpectedRevenueByStage(Lead.Stage.PROPOSITION);
        stats.put("expectedRevenue", expectedRevenue != null ? expectedRevenue : BigDecimal.ZERO);

        // Activities
        LocalDate today = LocalDate.now();
        stats.put("activitiesOverdue", activityRepository.countOverdueActivities(today));
        stats.put("activitiesToday", activityRepository.countTodayActivities(today));

        // Conversion rate
        long total = (Long) stats.get("totalLeads") + (Long) stats.get("wonLeads") + (Long) stats.get("lostLeads");
        long won = (Long) stats.get("wonLeads");
        double rate = total > 0 ? (double) won / total * 100 : 0;
        stats.put("conversionRate", Math.round(rate * 10.0) / 10.0);

        // Stage breakdown
        var stageBreakdown = java.util.Arrays.stream(Lead.Stage.values()).map(stage -> {
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("stage", stage.name().toLowerCase());
            s.put("count", leadRepository.countByStage(stage));
            s.put("revenue", leadRepository.sumExpectedRevenueByStage(stage));
            return s;
        }).toList();
        stats.put("leadsByStage", stageBreakdown);

        return ResponseEntity.ok(stats);
    }
}
