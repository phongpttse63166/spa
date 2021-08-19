package swp490.spa.dto.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RatingRequest {
    private Integer staffId;
    private Integer ratingId;
    private String comment;
    private Integer rate;
}
