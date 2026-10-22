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
        // Guard: Prevent overwriting any existing administrator account or credentials
        if (userAccountRepository.existsByUsername(adminUsername) || userAccountRepository.existsByRole(UserRole.ADMIN)) {
            log.info("Administrator account is already configured in database. Preserving existing credentials.");
            return;
        }

        // Require PESTRISK_ADMIN_PASSWORD to be explicitly configured outside tests
        if (adminPassword == null || adminPassword.isBlank()) {
            log.warn("No administrator account found in database. PESTRISK_ADMIN_PASSWORD is not configured. " +
                     "Skipping automatic provisioning. Configure PESTRISK_ADMIN_PASSWORD to initialize administrator credentials.");
            return;
        }

        UserAccount admin = new UserAccount(
                adminUsername,
                passwordEncoder.encode(adminPassword),
                UserRole.ADMIN
        );
        userAccountRepository.save(admin);
        log.info("Successfully initialized administrator account '{}' with role ADMIN.", adminUsername);
    }
}
