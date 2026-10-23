package com.pestrisk.config;

import com.pestrisk.entity.UserAccount;
import com.pestrisk.enums.UserRole;
import com.pestrisk.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserInitializer.class);

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public AdminUserInitializer(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            @Value("${pestrisk.admin.username:admin}") String adminUsername,
            @Value("${pestrisk.admin.password:}") String adminPassword) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        String effectiveAdminPass = (adminPassword != null && !adminPassword.isBlank()) 
                ? adminPassword 
                : "AdminPassword123";

        // Provision Administrator account if not already configured
        if (!userAccountRepository.existsByUsername(adminUsername) && !userAccountRepository.existsByRole(UserRole.ADMIN)) {
            UserAccount admin = new UserAccount(
                    adminUsername,
                    passwordEncoder.encode(effectiveAdminPass),
                    UserRole.ADMIN
            );
            userAccountRepository.save(admin);
            log.info("Successfully initialized administrator account '{}' with role ADMIN.", adminUsername);
        } else {
            log.info("Administrator account is already configured in database. Preserving existing credentials.");
        }

        // Provision default demonstration staff user STAFF-101 if not present
        String defaultStaffUsername = "STAFF-101";
        if (!userAccountRepository.existsByUsername(defaultStaffUsername)) {
            UserAccount staff = new UserAccount(
                    defaultStaffUsername,
                    passwordEncoder.encode("StaffPassword123"),
                    UserRole.STAFF
            );
            userAccountRepository.save(staff);
            log.info("Successfully initialized demonstration staff account '{}' with role STAFF.", defaultStaffUsername);
        }
    }
}
