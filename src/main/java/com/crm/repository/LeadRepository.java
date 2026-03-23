package com.crm.repository;

import com.crm.entity.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    Page<Lead> findByActiveTrue(Pageable pageable);

    Page<Lead> findByStageAndActiveTrue(Lead.Stage stage, Pageable pageable);

    @Query("SELECT l FROM Lead l WHERE l.active = true AND " +
           "(LOWER(l.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(l.contactName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(l.company) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Lead> searchLeads(@Param("search") String search, Pageable pageable);

    @Query("SELECT l FROM Lead l WHERE l.active = true AND l.assignedTo.id = :userId")
    Page<Lead> findByAssignedToId(@Param("userId") Long userId, Pageable pageable);

    List<Lead> findByStageAndActiveTrue(Lead.Stage stage);

    @Query("SELECT COUNT(l) FROM Lead l WHERE l.stage = :stage AND l.active = true")
    Long countByStage(@Param("stage") Lead.Stage stage);

    @Query("SELECT COALESCE(SUM(l.expectedRevenue), 0) FROM Lead l WHERE l.stage = :stage AND l.active = true")
    BigDecimal sumExpectedRevenueByStage(@Param("stage") Lead.Stage stage);

    @Query("SELECT COALESCE(SUM(l.expectedRevenue), 0) FROM Lead l WHERE l.stage = 'WON'")
    BigDecimal totalWonRevenue();
}
