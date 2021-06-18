package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {
}
