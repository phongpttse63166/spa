package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.Spa;
import swp490.spa.entities.Status;

@Repository
public interface SpaRepository extends JpaRepository<Spa, Integer> {
    Page<Spa> findByStatus(Status status, Pageable pageable);
}
