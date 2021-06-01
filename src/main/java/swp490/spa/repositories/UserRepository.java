package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
//    User findByPhoneAndPasswordAndRole(String phone, String password, String role);
    User findByPhone(String phone);
}
