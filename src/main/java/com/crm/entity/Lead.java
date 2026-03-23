package com.crm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "leads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(name = "contact_name", nullable = false)
    private String contactName;

    private String email;
    private String phone;
    private String company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Stage stage = Stage.NEW;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.NORMAL;

    @Column(name = "expected_revenue", precision = 15, scale = 2)
    private BigDecimal expectedRevenue = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2)
    private BigDecimal probability = BigDecimal.valueOf(20);

    private String source;

    @Column(name = "closing_date")
    private LocalDate closingDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_active")
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "lead_tags",
        joinColumns = @JoinColumn(name = "lead_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    public enum Stage {
        NEW, QUALIFIED, PROPOSITION, WON, LOST
    }

    public enum Priority {
        LOW, NORMAL, HIGH, VERY_HIGH
    }
}
