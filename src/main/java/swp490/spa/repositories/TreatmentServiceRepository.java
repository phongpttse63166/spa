package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.TreatmentService;

@Repository
public interface TreatmentServiceRepository extends JpaRepository<TreatmentService, Integer> {
    @Query("FROM TreatmentService t WHERE t.spaTreatment.id = ?1")
    Page<TreatmentService> findBySpaTreatmentId(Integer spaTreatmentId, Pageable pageable);
}
