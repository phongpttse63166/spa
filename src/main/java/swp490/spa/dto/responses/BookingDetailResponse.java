package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import swp490.spa.entities.Booking;
import swp490.spa.entities.SpaPackage;
import swp490.spa.entities.SpaTreatment;
import swp490.spa.entities.Type;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingDetailResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("total_time")
    private Integer totalTime;
    @JsonProperty("type")
    private Type type;
    @JsonProperty("total_price")
    private Double totalPrice;
    @JsonProperty("booking")
    private Booking booking;
    @JsonProperty("treatment")
    private SpaTreatment spaTreatment;
    @JsonProperty("spa_package")
    private SpaPackage spaPackage;
}
