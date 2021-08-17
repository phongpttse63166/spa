package swp490.spa.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Rating;
import swp490.spa.entities.StatusRating;
import swp490.spa.utils.support.templates.Constant;
import swp490.spa.utils.support.templates.LoggingTemplate;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Service
public class CheckRatingExpiredService {
    Logger LOGGER = LogManager.getLogger(CheckRatingExpiredService.class);
    @Autowired
    private RatingService ratingService;

    public CheckRatingExpiredService(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @Scheduled(cron = "0 0 7 * * *", zone = Constant.ZONE_ID)
    public void triggerCheckExpired() {
        Date currentDate = Date.valueOf(LocalDate.now(ZoneId.of(Constant.ZONE_ID)));
        List<Rating> ratings = ratingService.findByStatusAndExpired(StatusRating.WAITING,currentDate);
        if(Objects.nonNull(ratings)){
            if(ratings.size()!=0){
                for (Rating rating : ratings) {
                    rating.setStatusRating(StatusRating.EXPIRED);
                    Rating ratingResult = ratingService.editRating(rating);
                    if(Objects.isNull(ratingResult)){
                        LOGGER.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.RATING));
                    }
                }
            }
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.RATING));
        }
    }
}
