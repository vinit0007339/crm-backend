package com.crm.service;

import com.crm.dto.LeadDto;
import com.crm.dto.PageResponse;
import com.crm.entity.Lead;
import com.crm.exception.ResourceNotFoundException;
import com.crm.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeadService {

    private final LeadRepository leadRepository;

    public PageResponse<LeadDto> getAllLeads(String search, String stage, int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        if (StringUtils.hasText(search)) {
            return PageResponse.from(leadRepository.searchLeads(search, pageable), LeadDto::from);
        } else if (StringUtils.hasText(stage)) {
            Lead.Stage leadStage = Lead.Stage.valueOf(stage.toUpperCase());
            return PageResponse.from(leadRepository.findByStageAndActiveTrue(leadStage, pageable), LeadDto::from);
        }

        return PageResponse.from(leadRepository.findByActiveTrue(pageable), LeadDto::from);
    }

    public LeadDto getLeadById(Long id) {
        return LeadDto.from(findById(id));
    }

    public List<Map<String, Object>> getKanbanData() {
        List<Map<String, Object>> columns = new ArrayList<>();
        for (Lead.Stage stage : Lead.Stage.values()) {
            List<Lead> leads = leadRepository.findByStageAndActiveTrue(stage);
            BigDecimal totalRevenue = leads.stream()
                .map(Lead::getExpectedRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> column = new LinkedHashMap<>();
            column.put("stage", stage.name().toLowerCase());
            column.put("label", formatStageLabel(stage));
            column.put("leads", leads.stream().map(LeadDto::from).toList());
            column.put("totalRevenue", totalRevenue);
            columns.add(column);
        }
        return columns;
    }

    @Transactional
    public LeadDto createLead(LeadDto dto) {
        Lead lead = Lead.builder()
            .name(dto.getName())
            .contactName(dto.getContactName())
            .email(dto.getEmail())
            .phone(dto.getPhone())
            .company(dto.getCompany())
            .stage(dto.getStage() != null ? Lead.Stage.valueOf(dto.getStage().toUpperCase()) : Lead.Stage.NEW)
            .priority(dto.getPriority() != null ? Lead.Priority.valueOf(dto.getPriority().toUpperCase()) : Lead.Priority.NORMAL)
            .expectedRevenue(dto.getExpectedRevenue() != null ? dto.getExpectedRevenue() : BigDecimal.ZERO)
            .probability(dto.getProbability() != null ? dto.getProbability() : BigDecimal.valueOf(20))
            .source(dto.getSource())
            .notes(dto.getNotes())
            .closingDate(dto.getClosingDate())
            .build();

        return LeadDto.from(leadRepository.save(lead));
    }

    @Transactional
    public LeadDto updateLead(Long id, LeadDto dto) {
        Lead lead = findById(id);

        if (dto.getName() != null) lead.setName(dto.getName());
        if (dto.getContactName() != null) lead.setContactName(dto.getContactName());
        if (dto.getEmail() != null) lead.setEmail(dto.getEmail());
        if (dto.getPhone() != null) lead.setPhone(dto.getPhone());
        if (dto.getCompany() != null) lead.setCompany(dto.getCompany());
        if (dto.getStage() != null) lead.setStage(Lead.Stage.valueOf(dto.getStage().toUpperCase()));
        if (dto.getPriority() != null) lead.setPriority(Lead.Priority.valueOf(dto.getPriority().toUpperCase()));
        if (dto.getExpectedRevenue() != null) lead.setExpectedRevenue(dto.getExpectedRevenue());
        if (dto.getProbability() != null) lead.setProbability(dto.getProbability());
        if (dto.getSource() != null) lead.setSource(dto.getSource());
        if (dto.getNotes() != null) lead.setNotes(dto.getNotes());
        if (dto.getClosingDate() != null) lead.setClosingDate(dto.getClosingDate());

        return LeadDto.from(leadRepository.save(lead));
    }

    @Transactional
    public LeadDto updateStage(Long id, String stage) {
        Lead lead = findById(id);
        lead.setStage(Lead.Stage.valueOf(stage.toUpperCase()));
        if (lead.getStage() == Lead.Stage.WON) {
            lead.setProbability(BigDecimal.valueOf(100));
            lead.setClosingDate(LocalDate.now());
        } else if (lead.getStage() == Lead.Stage.LOST) {
            lead.setProbability(BigDecimal.ZERO);
        }
        return LeadDto.from(leadRepository.save(lead));
    }

    @Transactional
    public LeadDto markWon(Long id) {
        return updateStage(id, "WON");
    }

    @Transactional
    public LeadDto markLost(Long id) {
        Lead lead = findById(id);
        lead.setStage(Lead.Stage.LOST);
        lead.setProbability(BigDecimal.ZERO);
        lead.setActive(false);
        return LeadDto.from(leadRepository.save(lead));
    }

    @Transactional
    public void deleteLead(Long id) {
        Lead lead = findById(id);
        lead.setActive(false);
        leadRepository.save(lead);
    }

    private Lead findById(Long id) {
        return leadRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lead not found: " + id));
    }

    private String formatStageLabel(Lead.Stage stage) {
        return switch (stage) {
            case NEW -> "New";
            case QUALIFIED -> "Qualified";
            case PROPOSITION -> "Proposition";
            case WON -> "Won";
            case LOST -> "Lost";
        };
    }
}
