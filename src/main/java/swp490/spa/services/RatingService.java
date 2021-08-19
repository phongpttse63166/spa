package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Rating;
import swp490.spa.entities.StatusRating;
import swp490.spa.repositories.RatingRepository;

import java.sql.Date;
import java.util.List;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    public Rating insertNewRating(Rating rating) {
        return this.ratingRepository.save(rating);
    }

    public Rating findByRatingId(Integer ratingId) {
        return this.ratingRepository.findById(ratingId).get();
    }

    public Rating editRating(Rating rating) {
        return this.ratingRepository.save(rating);
    }

    public List<Rating> findAllByStatusOrderByDate(StatusRating statusRating) {
        return this.ratingRepository.findByStatusRatingOrderByCreateTimeDesc(statusRating);
    }

    public List<Rating> findByDateAndStatus(StatusRating statusRating, Date date) {
        return this.ratingRepository.findByStatusRatingAndCreateTime(statusRating, date);
    }

    public List<Rating> findByStatusAndExpired(StatusRating status, Date currentDate) {
        return this.ratingRepository.findByStatusRatingAndExpireTime(status, currentDate);
    }

    public List<Rating> findByRateAndSpa(Integer rate, Integer spaId) {
        return this.ratingRepository.findByRateAndSpa(rate, spaId);
    }
}
