package com.crm.controller;

import com.crm.dto.ContactDto;
import com.crm.dto.PageResponse;
import com.crm.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<PageResponse<ContactDto>> getAll(
        @RequestParam(defaultValue = "") String search,
        @RequestParam(defaultValue = "") String type,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(contactService.getAllContacts(search, type, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.getContactById(id));
    }

    @PostMapping
    public ResponseEntity<ContactDto> create(@Valid @RequestBody ContactDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactService.createContact(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactDto> update(@PathVariable Long id, @RequestBody ContactDto dto) {
        return ResponseEntity.ok(contactService.updateContact(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}
