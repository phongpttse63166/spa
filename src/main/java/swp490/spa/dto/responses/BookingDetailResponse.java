package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import swp490.spa.entities.*;

import java.util.List;

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
    @JsonProperty("status_booking")
    private StatusBooking statusBooking;
    @JsonProperty("booking")
    @JsonIgnore
    private Booking booking;
    @JsonProperty("treatment")
    private SpaTreatment spaTreatment;
    @JsonProperty("spa_package")
    private SpaPackage spaPackage;
    @JsonProperty("booking_detail_steps")
    private List<BookingDetailStepResponse> bookingDetailSteps;
}
