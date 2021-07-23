package swp490.spa.dto.responses;

import lombok.*;
import swp490.spa.entities.Rating;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TotalRatingResponse implements Serializable {
    private String staff;
    private List<Rating> ratingList;
}
