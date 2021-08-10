package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import swp490.spa.entities.Staff;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StaffRatingResponse implements Serializable{
    @JsonProperty("staff")
    Staff staff;
    @JsonProperty("average_rate")
    Double averageRateValue;
}
