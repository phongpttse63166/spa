package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Rating;
import swp490.spa.repositories.RatingRepository;

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
}
