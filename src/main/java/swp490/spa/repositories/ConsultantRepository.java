package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.Consultant;

@Repository
public interface ConsultantRepository extends JpaRepository<Consultant, Integer>{
    @Query("FROM Consultant c WHERE c.user.id = ?1")
    Consultant findConsultantByUserId(Integer userId);
}
