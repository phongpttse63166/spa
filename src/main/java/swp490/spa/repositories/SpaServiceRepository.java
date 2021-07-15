package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.SpaService;
import swp490.spa.entities.Status;
import swp490.spa.entities.Type;

@Repository
public interface SpaServiceRepository extends JpaRepository<SpaService, Integer> {
    @Query("FROM SpaService s where s.status = ?1 and s.name like %?2% ORDER BY s.id ASC")
    Page<SpaService> findByStatus(Status status, String search, Pageable pageable);

    Page<SpaService> findByTypeAndNameContainingAndStatusOrderById(Type type, String search,
                                                                   Status available, Pageable pageable);
}
