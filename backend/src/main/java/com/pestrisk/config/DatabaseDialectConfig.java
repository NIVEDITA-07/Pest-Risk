package com.pestrisk.config;

import org.hibernate.cfg.AvailableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * Automatically resolves the appropriate Hibernate Dialect based on the configured datasource URL.
 * Prevents SQLiteDialect from incorrectly being used with PostgreSQL or MySQL in production.
 */
@Configuration
public class DatabaseDialectConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseDialectConfig.class);

    @Bean
    public HibernatePropertiesCustomizer hibernateDialectCustomizer(Environment env) {
        return (Map<String, Object> hibernateProperties) -> {
            String explicitDialect = env.getProperty("JPA_DIALECT");
            if (explicitDialect != null && !explicitDialect.isBlank()) {
                log.info("Using explicitly configured JPA dialect: {}", explicitDialect);
                hibernateProperties.put(AvailableSettings.DIALECT, explicitDialect);
                return;
            }

            String dbUrl = env.getProperty("spring.datasource.url", "");
            if (dbUrl.contains("postgresql")) {
                log.info("Detected PostgreSQL datasource URL. Setting PostgreSQLDialect automatically.");
                hibernateProperties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
            } else if (dbUrl.contains("mysql")) {
                log.info("Detected MySQL datasource URL. Setting MySQLDialect automatically.");
                hibernateProperties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQLDialect");
            } else if (dbUrl.contains("sqlite")) {
                log.info("Detected SQLite datasource URL. Setting SQLiteDialect automatically.");
                hibernateProperties.put(AvailableSettings.DIALECT, "org.hibernate.community.dialect.SQLiteDialect");
            }
        };
    }
}
