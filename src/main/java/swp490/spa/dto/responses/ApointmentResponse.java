package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import swp490.spa.entities.BookingDetail;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApointmentResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("description")
    private String description;
    @JsonProperty("expectation")
    private String expectation;
    @JsonProperty("result")
    private String result;
    @JsonProperty("note")
    private String note;
    @JsonProperty("booking_detail_id")
    private BookingDetail bookingDetail;
}
