package com.crm.config;

import com.crm.entity.Activity;
import com.crm.entity.Contact;
import com.crm.entity.Lead;
import com.crm.entity.User;
import com.crm.repository.ActivityRepository;
import com.crm.repository.ContactRepository;
import com.crm.repository.LeadRepository;
import com.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData(
        UserRepository userRepo,
        ContactRepository contactRepo,
        LeadRepository leadRepo,
        ActivityRepository activityRepo
    ) {
        return args -> {
            if (userRepo.count() > 0) {
                log.info("Database already initialized, skipping seed data.");
                return;
            }

            log.info("Initializing seed data...");

            // Create Users
            User admin = userRepo.save(User.builder()
                .name("Admin User")
                .email("admin@demo.com")
                .password(passwordEncoder.encode("admin123"))
                .role(User.Role.ADMIN)
                .build());

            User alice = userRepo.save(User.builder()
                .name("Alice Johnson")
                .email("alice@demo.com")
                .password(passwordEncoder.encode("alice123"))
                .role(User.Role.MANAGER)
                .build());

            User bob = userRepo.save(User.builder()
                .name("Bob Smith")
                .email("bob@demo.com")
                .password(passwordEncoder.encode("bob123"))
                .role(User.Role.SALESPERSON)
                .build());

            // Create Contacts
            Contact acme = contactRepo.save(Contact.builder()
                .name("Acme Corp").type(Contact.ContactType.COMPANY)
                .email("info@acmecorp.com").phone("+1-555-001-0001")
                .city("New York").country("USA")
                .build());

            Contact john = contactRepo.save(Contact.builder()
                .name("John Doe").type(Contact.ContactType.INDIVIDUAL)
                .email("john@acmecorp.com").phone("+1-555-001-0002")
                .jobPosition("CEO").company(acme)
                .build());

            Contact techco = contactRepo.save(Contact.builder()
                .name("TechCo Inc").type(Contact.ContactType.COMPANY)
                .email("hello@techco.com")
                .city("San Francisco").country("USA")
                .build());

            Contact sarah = contactRepo.save(Contact.builder()
                .name("Sarah Lee").type(Contact.ContactType.INDIVIDUAL)
                .email("sarah@techco.com").phone("+1-555-003-0003")
                .jobPosition("CTO").company(techco)
                .build());

            // Create Leads
            Lead lead1 = leadRepo.save(Lead.builder()
                .name("Enterprise Software Deal")
                .contactName("John Doe").email("john@acmecorp.com")
                .company("Acme Corp").stage(Lead.Stage.NEW)
                .priority(Lead.Priority.HIGH)
                .expectedRevenue(BigDecimal.valueOf(45000))
                .probability(BigDecimal.valueOf(20))
                .source("website").assignedTo(alice)
                .build());

            Lead lead2 = leadRepo.save(Lead.builder()
                .name("Cloud Migration Project")
                .contactName("Sarah Lee").email("sarah@techco.com")
                .company("TechCo Inc").stage(Lead.Stage.QUALIFIED)
                .priority(Lead.Priority.NORMAL)
                .expectedRevenue(BigDecimal.valueOf(62000))
                .probability(BigDecimal.valueOf(50))
                .source("referral").assignedTo(bob)
                .build());

            Lead lead3 = leadRepo.save(Lead.builder()
                .name("Annual Consulting Retainer")
                .contactName("Mike Brown").email("mike@startup.io")
                .company("StartupXYZ").stage(Lead.Stage.WON)
                .priority(Lead.Priority.NORMAL)
                .expectedRevenue(BigDecimal.valueOf(24000))
                .probability(BigDecimal.valueOf(100))
                .assignedTo(alice)
                .build());

            Lead lead4 = leadRepo.save(Lead.builder()
                .name("Custom Integration Build")
                .contactName("Tom Wilson").email("tom@datasoft.com")
                .company("DataSoft").stage(Lead.Stage.PROPOSITION)
                .priority(Lead.Priority.HIGH)
                .expectedRevenue(BigDecimal.valueOf(95000))
                .probability(BigDecimal.valueOf(70))
                .source("email").assignedTo(bob)
                .build());

            // Create Activities
            activityRepo.save(Activity.builder()
                .type(Activity.ActivityType.CALL)
                .summary("Discovery call with John Doe - Enterprise deal")
                .dueDate(LocalDate.now())
                .lead(lead1).assignedTo(alice)
                .build());

            activityRepo.save(Activity.builder()
                .type(Activity.ActivityType.EMAIL)
                .summary("Send proposal for Cloud Migration Project")
                .dueDate(LocalDate.now().plusDays(1))
                .lead(lead2).assignedTo(bob)
                .build());

            activityRepo.save(Activity.builder()
                .type(Activity.ActivityType.MEETING)
                .summary("Demo presentation for Custom Integration")
                .dueDate(LocalDate.now().plusDays(3))
                .lead(lead4).assignedTo(bob)
                .build());

            activityRepo.save(Activity.builder()
                .type(Activity.ActivityType.TODO)
                .summary("Prepare contract for Consulting Retainer")
                .dueDate(LocalDate.now().minusDays(2))
                .lead(lead3).assignedTo(alice)
                .build());

            log.info("Seed data initialized successfully. Login with admin@demo.com / admin123");
        };
    }
}
