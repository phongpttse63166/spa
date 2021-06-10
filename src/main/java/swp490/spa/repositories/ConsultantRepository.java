package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.Consultant;

@Repository
public interface ConsultantRepository extends JpaRepository<Consultant, Integer>{
}
