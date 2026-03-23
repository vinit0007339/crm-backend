package com.crm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContactType type = ContactType.INDIVIDUAL;

    private String email;
    private String phone;
    private String mobile;
    private String website;

    @Column(name = "job_position")
    private String jobPosition;

    // Address
    private String street;
    private String city;
    private String state;
    private String country;
    private String zip;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_active")
    private boolean active = true;

    // Self-referential: contact can belong to a company
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Contact company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "contact_tags",
        joinColumns = @JoinColumn(name = "contact_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    public enum ContactType {
        INDIVIDUAL, COMPANY
    }
}
