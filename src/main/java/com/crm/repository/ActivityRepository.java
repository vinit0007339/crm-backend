package com.crm.repository;

import com.crm.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Page<Activity> findAll(Pageable pageable);

    Page<Activity> findByDone(boolean done, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.done = false AND a.dueDate < :today")
    List<Activity> findOverdueActivities(@Param("today") LocalDate today);

    @Query("SELECT COUNT(a) FROM Activity a WHERE a.done = false AND a.dueDate < :today")
    Long countOverdueActivities(@Param("today") LocalDate today);

    @Query("SELECT COUNT(a) FROM Activity a WHERE a.done = false AND a.dueDate = :today")
    Long countTodayActivities(@Param("today") LocalDate today);

    Page<Activity> findByType(Activity.ActivityType type, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.assignedTo.id = :userId AND a.done = false ORDER BY a.dueDate ASC")
    List<Activity> findPendingByUser(@Param("userId") Long userId, Pageable pageable);
}
