package com.crm.controller;

import com.crm.dto.ActivityDto;
import com.crm.dto.PageResponse;
import com.crm.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    public ResponseEntity<PageResponse<ActivityDto>> getAll(
        @RequestParam(required = false) Boolean isDone,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(activityService.getAllActivities(isDone, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ActivityDto> create(@Valid @RequestBody ActivityDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(activityService.createActivity(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityDto> update(@PathVariable Long id, @RequestBody ActivityDto dto) {
        return ResponseEntity.ok(activityService.updateActivity(id, dto));
    }

    @PatchMapping("/{id}/done")
    public ResponseEntity<ActivityDto> markDone(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.markDone(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}
