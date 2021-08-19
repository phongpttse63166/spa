package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RateWithCountRatingResponse {
    @JsonProperty("rate")
    private Integer rate;
    @JsonProperty("count_rating")
    private Integer countRating;
}
