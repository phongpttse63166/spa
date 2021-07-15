package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.SpaTreatment;

@Repository
public interface SpaTreatmentRepository extends JpaRepository<SpaTreatment, Integer> {
    @Query("FROM SpaTreatment s WHERE s.name LIKE %?1% ORDER BY s.id ASC")
    Page<SpaTreatment> findAllSpaTreatment(String search, Pageable pageable);

    @Query("FROM SpaTreatment s WHERE s.spaPackage.id = ?1 and s.name LIKE %?2% ORDER BY s.id ASC")
    Page<SpaTreatment> findByPackageId(Integer packageId, String search, Pageable pageable);

    SpaTreatment findBySpaPackage_Id(Integer packageId);

}
