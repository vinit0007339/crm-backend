package com.crm.dto;

import com.crm.entity.Lead;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String contactName;

    private String email;
    private String phone;
    private String company;
    private String stage;
    private String priority;
    private BigDecimal expectedRevenue;
    private BigDecimal probability;
    private String source;
    private String notes;
    private LocalDate closingDate;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDto assignedTo;

    public static LeadDto from(Lead lead) {
        return LeadDto.builder()
            .id(lead.getId())
            .name(lead.getName())
            .contactName(lead.getContactName())
            .email(lead.getEmail())
            .phone(lead.getPhone())
            .company(lead.getCompany())
            .stage(lead.getStage().name().toLowerCase())
            .priority(lead.getPriority().name().toLowerCase())
            .expectedRevenue(lead.getExpectedRevenue())
            .probability(lead.getProbability())
            .source(lead.getSource())
            .notes(lead.getNotes())
            .closingDate(lead.getClosingDate())
            .isActive(lead.isActive())
            .createdAt(lead.getCreatedAt())
            .updatedAt(lead.getUpdatedAt())
            .assignedTo(lead.getAssignedTo() != null ? UserDto.from(lead.getAssignedTo()) : null)
            .build();
    }
}
