package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.SpaService;
import swp490.spa.entities.Status;

@Repository
public interface SpaServiceRepository extends JpaRepository<SpaService, Integer> {
    @Query("FROM SpaService s where s.spa.id = ?1 and s.status = ?2 and s.name LIKE %?3%")
    Page<SpaService> findBySpaIdAndStatus(Integer spaId, Status status, String search, Pageable pageable);
}
