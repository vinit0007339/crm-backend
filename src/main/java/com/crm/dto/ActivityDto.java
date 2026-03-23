package com.crm.dto;

import com.crm.entity.Activity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDto {
    private Long id;

    @NotNull
    private String type;

    @NotBlank
    private String summary;

    @NotNull
    private LocalDate dueDate;

    private LocalDate doneDate;
    private boolean isDone;
    private String notes;
    private Long leadId;
    private Long contactId;
    private Long assignedToId;
    private UserDto assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ActivityDto from(Activity a) {
        return ActivityDto.builder()
            .id(a.getId())
            .type(a.getType().name().toLowerCase())
            .summary(a.getSummary())
            .dueDate(a.getDueDate())
            .doneDate(a.getDoneDate())
            .isDone(a.isDone())
            .notes(a.getNotes())
            .leadId(a.getLead() != null ? a.getLead().getId() : null)
            .contactId(a.getContact() != null ? a.getContact().getId() : null)
            .assignedTo(a.getAssignedTo() != null ? UserDto.from(a.getAssignedTo()) : null)
            .createdAt(a.getCreatedAt())
            .updatedAt(a.getUpdatedAt())
            .build();
    }
}
