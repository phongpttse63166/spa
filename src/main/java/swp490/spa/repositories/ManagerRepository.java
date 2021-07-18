package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.Manager;
import swp490.spa.entities.Status;

import java.util.List;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Integer> {
    @Query("FROM Manager m WHERE m.user.id = ?1")
    Manager findByUserId(Integer userId);

    List<Manager> findBySpa_IdAndStatusOrderByIdAsc(Integer spaId, Status status);
}
