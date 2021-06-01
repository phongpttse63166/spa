package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.SpaAddress;

@Repository
public interface SpaAddressRepository extends JpaRepository<SpaAddress, Integer> {
    @Query("FROM SpaAddress s where s.spa.id = ?1")
    Page<SpaAddress> findBySpaId(Integer spaId, Pageable pageable);
}
