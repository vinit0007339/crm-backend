package com.crm.service;

import com.crm.dto.ContactDto;
import com.crm.dto.PageResponse;
import com.crm.entity.Contact;
import com.crm.exception.ResourceNotFoundException;
import com.crm.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactService {

    private final ContactRepository contactRepository;

    public PageResponse<ContactDto> getAllContacts(String search, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        if (StringUtils.hasText(search)) {
            return PageResponse.from(contactRepository.searchContacts(search, pageable), ContactDto::from);
        } else if (StringUtils.hasText(type)) {
            Contact.ContactType contactType = Contact.ContactType.valueOf(type.toUpperCase());
            return PageResponse.from(contactRepository.findByTypeAndActiveTrue(contactType, pageable), ContactDto::from);
        }

        return PageResponse.from(contactRepository.findByActiveTrue(pageable), ContactDto::from);
    }

    public ContactDto getContactById(Long id) {
        return ContactDto.from(findById(id));
    }

    @Transactional
    public ContactDto createContact(ContactDto dto) {
        Contact contact = Contact.builder()
            .name(dto.getName())
            .type(dto.getType() != null ? Contact.ContactType.valueOf(dto.getType().toUpperCase()) : Contact.ContactType.INDIVIDUAL)
            .email(dto.getEmail())
            .phone(dto.getPhone())
            .mobile(dto.getMobile())
            .website(dto.getWebsite())
            .jobPosition(dto.getJobPosition())
            .street(dto.getStreet())
            .city(dto.getCity())
            .state(dto.getState())
            .country(dto.getCountry())
            .zip(dto.getZip())
            .notes(dto.getNotes())
            .build();

        if (dto.getCompanyId() != null) {
            contact.setCompany(findById(dto.getCompanyId()));
        }

        return ContactDto.from(contactRepository.save(contact));
    }

    @Transactional
    public ContactDto updateContact(Long id, ContactDto dto) {
        Contact contact = findById(id);

        if (dto.getName() != null) contact.setName(dto.getName());
        if (dto.getType() != null) contact.setType(Contact.ContactType.valueOf(dto.getType().toUpperCase()));
        if (dto.getEmail() != null) contact.setEmail(dto.getEmail());
        if (dto.getPhone() != null) contact.setPhone(dto.getPhone());
        if (dto.getMobile() != null) contact.setMobile(dto.getMobile());
        if (dto.getWebsite() != null) contact.setWebsite(dto.getWebsite());
        if (dto.getJobPosition() != null) contact.setJobPosition(dto.getJobPosition());
        if (dto.getCity() != null) contact.setCity(dto.getCity());
        if (dto.getCountry() != null) contact.setCountry(dto.getCountry());
        if (dto.getNotes() != null) contact.setNotes(dto.getNotes());

        return ContactDto.from(contactRepository.save(contact));
    }

    @Transactional
    public void deleteContact(Long id) {
        Contact contact = findById(id);
        contact.setActive(false);
        contactRepository.save(contact);
    }

    private Contact findById(Long id) {
        return contactRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Contact not found: " + id));
    }
}
