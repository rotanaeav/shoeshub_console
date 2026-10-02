package kh.com.shoeshub.features.user;

import kh.com.shoeshub.features.user.repository.UserRepository;
import kh.com.shoeshub.features.user.repository.UserRepositoryImpl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

public class UserUI {

    void main() {

        UserRepository userRepository = new UserRepositoryImpl();

        // CREATE TEST USER
        User user = new User();

        user.setRole(UserRole.CUSTOMER);
        user.setFullName("Test User");
        user.setUsername("test_" + UUID.randomUUID().toString().substring(0, 8));
        user.setPasswordHash("test_hash_123");
        user.setPhone("0123456789");
        user.setDateOfBirth(LocalDate.of(2000, 12, 24));
        user.setGender("MALE");
        user.setAddress("Phnom Penh, Cambodia");

        // ADD
        System.out.println("\n========== ADD USER ==========");

        User savedUser = userRepository.save(user);

        System.out.println("Saved ID: " + savedUser.getId());
        System.out.println("Saved Username: " + savedUser.getUsername());

        UUID id = savedUser.getId();

        // FIND BY ID
        System.out.println("\n========== FIND BY ID ==========");

        userRepository.findById(id).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("User not found")
        );

        // FIND BY USERNAME
        System.out.println("\n========== FIND BY USERNAME ==========");

        userRepository.findByUsername(user.getUsername()).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("User not found")
        );

        // FIND ALL
        System.out.println("\n========== FIND ALL ==========");

        userRepository.findAll().forEach(System.out::println);

        // UPDATE
        System.out.println("\n========== UPDATE USER ==========");

        user.setFullName("Updated Test User");
        user.setPhone("0987654321");
        user.setAddress("Siem Reap, Cambodia");

        userRepository.update(id, user);

        userRepository.findById(id).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("User not found")
        );

        // SOFT DELETE
        System.out.println("\n========== SOFT DELETE ==========");

        boolean deleted = userRepository.softDelete(id);

        System.out.println("Soft deleted: " + deleted);

        // FIND BY ID AFTER DELETE
        System.out.println("\n========== FIND DELETED USER ==========");

        userRepository.findById(id).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("User not found (Expected)")
        );

        // FIND BY ID INCLUDE DELETED
        System.out.println("\n========== FIND INCLUDING DELETED ==========");

        userRepository.findByIdIncludeDeleted(id).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("User not found")
        );

        // RESTORE
        System.out.println("\n========== RESTORE USER ==========");

        userRepository.restore(id).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Restore failed")
        );

        // PERMANENT DELETE
        System.out.println("\n========== PERMANENT DELETE ==========");

        userRepository.softDelete(id);

        userRepository.deleteById(id);

        System.out.println("User permanently deleted");

        // VERIFY DELETE
        System.out.println("\n========== VERIFY DELETE ==========");

        userRepository.findByIdIncludeDeleted(id).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("User successfully removed")
        );
    }
}
