package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.Category;
import swp490.spa.entities.Status;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Page<Category> findByStatusOrderById(Status status, Pageable pageable);

    @Query("FROM Category c WHERE c.id = ?1")
    Category findByCategoryId(Integer categoryId);

    Page<Category> findBySpa_IdAndStatusAndNameContainingOrderById(Integer spaId, Status status,
                                                  String search, Pageable pageable);

    Page<Category> findBySpa_IdAndStatusOrderById(Integer spaId, Status status, Pageable pageable);
}
