package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.SpaTreatment;

@Repository
public interface SpaTreatmentRepository extends JpaRepository<SpaTreatment, Integer> {
    @Query("FROM SpaTreatment s WHERE s.spa.id = ?1 and s.name LIKE %?2%")
    Page<SpaTreatment> findTreatmentBySpaId(Integer spaId, String search, Pageable pageable);

    @Query("FROM SpaTreatment s WHERE s.spaPackage.id = ?1 and s.name LIKE %?2%")
    Page<SpaTreatment> findByPackageId(Integer packageId, String search, Pageable pageable);
}
