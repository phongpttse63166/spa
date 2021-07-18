package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.Staff;
import swp490.spa.entities.Status;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    @Query("FROM Staff s WHERE s.user.id = ?1")
    Staff findByUserId(Integer userId);

    List<Staff> findBySpa_IdAndStatus(Integer spaId, Status status);

    @Query("FROM Staff s WHERE s.spa.id = ?1 AND s.user.fullname LIKE %?2% ORDER BY s.id")
    List<Staff> findStaffBySpaIdAndNameLike(Integer spaId, String search);
}
