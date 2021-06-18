package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.RatingResponse;
import swp490.spa.entities.Rating;

@Mapper
public interface RatingMapper {
    RatingResponse changeToRatingResponse(Rating rating);
}
