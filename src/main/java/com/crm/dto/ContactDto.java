package com.crm.dto;

import com.crm.entity.Contact;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {
    private Long id;

    @NotBlank
    private String name;

    private String type;
    private String email;
    private String phone;
    private String mobile;
    private String website;
    private String company;
    private Long companyId;
    private String jobPosition;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String notes;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDto assignedTo;

    public static ContactDto from(Contact contact) {
        return ContactDto.builder()
            .id(contact.getId())
            .name(contact.getName())
            .type(contact.getType().name().toLowerCase())
            .email(contact.getEmail())
            .phone(contact.getPhone())
            .mobile(contact.getMobile())
            .website(contact.getWebsite())
            .company(contact.getCompany() != null ? contact.getCompany().getName() : null)
            .companyId(contact.getCompany() != null ? contact.getCompany().getId() : null)
            .jobPosition(contact.getJobPosition())
            .street(contact.getStreet())
            .city(contact.getCity())
            .state(contact.getState())
            .country(contact.getCountry())
            .zip(contact.getZip())
            .notes(contact.getNotes())
            .isActive(contact.isActive())
            .createdAt(contact.getCreatedAt())
            .updatedAt(contact.getUpdatedAt())
            .assignedTo(contact.getAssignedTo() != null ? UserDto.from(contact.getAssignedTo()) : null)
            .build();
    }
}
