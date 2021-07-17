package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.Consultant;

import java.util.List;

@Repository
public interface ConsultantRepository extends JpaRepository<Consultant, Integer>{
    @Query("FROM Consultant c WHERE c.user.id = ?1")
    Consultant findConsultantByUserId(Integer userId);

    List<Consultant> findBySpa_Id(Integer spaId);

    @Query("FROM Consultant c WHERE c.spa.id = ?1 AND c.user.fullname LIKE %?2% ORDER BY c.id")
    List<Consultant> findConsultantBySpaIdAndNameLike(Integer spaId, String search);
}
