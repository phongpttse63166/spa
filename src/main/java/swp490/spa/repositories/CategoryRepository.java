package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import swp490.spa.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Page<Category> findByStatus(String status, Pageable pageable);
}
