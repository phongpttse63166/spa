package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.Rating;
import swp490.spa.entities.StatusRating;

import java.sql.Date;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {
    List<Rating> findByStatusRatingOrderByCreateTimeDesc(StatusRating status);

    List<Rating> findByStatusRatingAndCreateTime(StatusRating statusRating, Date date);

    List<Rating> findByStatusRatingAndExpireTime(StatusRating statusRating, Date date);
}
