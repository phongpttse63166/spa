package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.repositories.RatingRepository;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;
}
