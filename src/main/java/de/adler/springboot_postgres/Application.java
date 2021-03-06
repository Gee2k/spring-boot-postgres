package de.adler.springboot_postgres;

import de.adler.springboot_postgres.database.entity.User;
import de.adler.springboot_postgres.database.entity.UserRole;
import de.adler.springboot_postgres.database.repository.UserRepository;
import de.adler.springboot_postgres.database.repository.UserRoleRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@SuppressWarnings("WeakerAccess")
@EnableAsync
@SpringBootApplication
public class Application {

    private static final Logger log = Logger.getLogger(Application.class);

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * This component is used at setup to make some basic entries into the database
     * This can be simplified to a Bean with Java 8+
     */
    @Component
    @Profile({"setup", "test"})
    public class UserSetup implements CommandLineRunner {

        final UserRepository userRepo;
        final UserRoleRepository roleRepo;

        public UserSetup(UserRepository userRepo, UserRoleRepository roleRepo) {
            this.userRepo = userRepo;
            this.roleRepo = roleRepo;
        }

        public void run(String... args) {
            User savedUser = userRepo.save(
                    new User("user", bCryptPasswordEncoder.encode("password"), "email1", true));
            roleRepo.save(new UserRole(savedUser.getUserid(), "USER"));
            User savedAdmin = userRepo.save(
                    new User("admin", bCryptPasswordEncoder.encode("admin"), "email2", true));
            roleRepo.save(new UserRole(savedAdmin.getUserid(), "ADMIN"));
        }
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}