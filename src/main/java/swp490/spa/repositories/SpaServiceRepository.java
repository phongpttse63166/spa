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
    @Query("FROM SpaService s WHERE s.id = ?1")
    SpaService findBySpaId(Integer spaId);

    @Query("FROM SpaService s where s.spa.id = ?1 and s.status = ?2 and s.name like %?3%")
    Page<SpaService> findBySpaIdAndStatus(Integer spaId, Status status, String search, Pageable pageable);

    Page<SpaService> findBySpa_IdAndTypeAndNameContainingAndStatus(Integer spaId, Type type, String search, Status available, Pageable pageable);
}
