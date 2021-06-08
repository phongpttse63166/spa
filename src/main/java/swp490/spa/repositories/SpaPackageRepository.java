package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.SpaPackage;
import swp490.spa.entities.Status;

@Repository
public interface SpaPackageRepository extends JpaRepository<SpaPackage, Integer> {
    @Query("FROM SpaPackage s WHERE s.spa.id = ?1 AND s.status = ?2")
    Page<SpaPackage> findSpaPackageBySpaIdAndStatus(Integer spaId, Status status, Pageable pageable);

    Page<SpaPackage> findAllByStatus(Status status, Pageable pageable);
}
