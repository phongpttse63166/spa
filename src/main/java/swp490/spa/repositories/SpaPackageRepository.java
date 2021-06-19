package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.SpaPackage;
import swp490.spa.entities.Status;

import java.util.List;

@Repository
public interface SpaPackageRepository extends JpaRepository<SpaPackage, Integer> {
    @Query("FROM SpaPackage s WHERE s.spa.id = ?1 AND s.status = ?2 AND s.name LIKE %?3%")
    Page<SpaPackage> findSpaPackageBySpaIdAndStatus(Integer spaId, Status status,
                                                    String search ,Pageable pageable);

    Page<SpaPackage> findAllByStatus(Status status, Pageable pageable);

    @Query("FROM SpaPackage s WHERE s.category.id = ?1 AND s.name LIKE %?2% ORDER BY s.createTime DESC ")
    Page<SpaPackage> findByCategory_IdOrderByCreateTimeDesc(Integer categoryId, Pageable pageable);

    List<SpaPackage> findByCategory_Id(Integer categoryId);
}
