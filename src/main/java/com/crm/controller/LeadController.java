package com.crm.controller;

import com.crm.dto.LeadDto;
import com.crm.dto.PageResponse;
import com.crm.service.LeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @GetMapping
    public ResponseEntity<PageResponse<LeadDto>> getAllLeads(
        @RequestParam(defaultValue = "") String search,
        @RequestParam(defaultValue = "") String stage,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ResponseEntity.ok(leadService.getAllLeads(search, stage, page, size, sortBy, sortDir));
    }

    @GetMapping("/kanban")
    public ResponseEntity<List<Map<String, Object>>> getKanban() {
        return ResponseEntity.ok(leadService.getKanbanData());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.getLeadById(id));
    }

    @PostMapping
    public ResponseEntity<LeadDto> create(@Valid @RequestBody LeadDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leadService.createLead(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeadDto> update(@PathVariable Long id, @RequestBody LeadDto dto) {
        return ResponseEntity.ok(leadService.updateLead(id, dto));
    }

    @PatchMapping("/{id}/stage")
    public ResponseEntity<LeadDto> updateStage(
        @PathVariable Long id,
        @RequestBody Map<String, String> body
    ) {
        return ResponseEntity.ok(leadService.updateStage(id, body.get("stage")));
    }

    @PatchMapping("/{id}/won")
    public ResponseEntity<LeadDto> markWon(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.markWon(id));
    }

    @PatchMapping("/{id}/lost")
    public ResponseEntity<LeadDto> markLost(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.markLost(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.noContent().build();
    }
}
