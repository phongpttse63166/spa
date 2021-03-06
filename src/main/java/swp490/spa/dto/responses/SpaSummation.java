package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpaSummation {
    @JsonProperty("month")
    private Integer month;
    @JsonProperty("count_finish_service")
    private Integer countFinishService;
}
