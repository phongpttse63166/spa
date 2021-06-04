package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.AccountRegister;

@Repository
public interface AccountRegisterRepository extends JpaRepository<AccountRegister, Integer> {
    AccountRegister findByPhone(String phone);
}
