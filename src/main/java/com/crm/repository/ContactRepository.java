package com.crm.repository;

import com.crm.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    Page<Contact> findByActiveTrue(Pageable pageable);

    Page<Contact> findByTypeAndActiveTrue(Contact.ContactType type, Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.active = true AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Contact> searchContacts(@Param("search") String search, Pageable pageable);
}
