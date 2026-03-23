package com.crm.service;

import com.crm.dto.ActivityDto;
import com.crm.dto.PageResponse;
import com.crm.entity.Activity;
import com.crm.entity.Lead;
import com.crm.exception.ResourceNotFoundException;
import com.crm.repository.ActivityRepository;
import com.crm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final LeadRepository leadRepository;

    public PageResponse<ActivityDto> getAllActivities(Boolean isDone, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());

        if (isDone != null) {
            return PageResponse.from(activityRepository.findByDone(isDone, pageable), ActivityDto::from);
        }

        return PageResponse.from(activityRepository.findAll(pageable), ActivityDto::from);
    }

    public ActivityDto getById(Long id) {
        return ActivityDto.from(findById(id));
    }

    @Transactional
    public ActivityDto createActivity(ActivityDto dto) {
        Activity activity = Activity.builder()
            .type(Activity.ActivityType.valueOf(dto.getType().toUpperCase()))
            .summary(dto.getSummary())
            .dueDate(dto.getDueDate())
            .notes(dto.getNotes())
            .done(false)
            .build();

        if (dto.getLeadId() != null) {
            Lead lead = leadRepository.findById(dto.getLeadId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));
            activity.setLead(lead);
        }

        return ActivityDto.from(activityRepository.save(activity));
    }

    @Transactional
    public ActivityDto updateActivity(Long id, ActivityDto dto) {
        Activity activity = findById(id);
        if (dto.getSummary() != null) activity.setSummary(dto.getSummary());
        if (dto.getDueDate() != null) activity.setDueDate(dto.getDueDate());
        if (dto.getNotes() != null) activity.setNotes(dto.getNotes());
        if (dto.getType() != null) activity.setType(Activity.ActivityType.valueOf(dto.getType().toUpperCase()));
        return ActivityDto.from(activityRepository.save(activity));
    }

    @Transactional
    public ActivityDto markDone(Long id) {
        Activity activity = findById(id);
        activity.setDone(true);
        activity.setDoneDate(LocalDate.now());
        return ActivityDto.from(activityRepository.save(activity));
    }

    @Transactional
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    private Activity findById(Long id) {
        return activityRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Activity not found: " + id));
    }
}
