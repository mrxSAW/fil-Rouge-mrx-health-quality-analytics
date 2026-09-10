package org.example.healthcarequalite.config;

import org.example.healthcarequalite.enums.Role;
import org.example.healthcarequalite.entity.User;
import org.example.healthcarequalite.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner createFirstAdmin() {
        return args -> {
            String adminEmail = "admin@healthquality.ma";

            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User();

                admin.setFirstName("Admin");
                admin.setLastName("Health Quality");
                admin.setUsername("admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("1234"));
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);

                System.out.println( "Premier administrateur créé : " + adminEmail );
            }
        };
    }
}