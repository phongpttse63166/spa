package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    @Query("FROM Admin a WHERE a.user.id = ?1")
    Admin findByUserId(Integer userId);
}
