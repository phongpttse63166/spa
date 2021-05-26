package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import swp490.spa.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByPhoneAndPasswordAndRole(String phone, String password, String role);
    User findUserByPhone(String phone);
}
