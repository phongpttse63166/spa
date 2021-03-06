package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import swp490.spa.entities.BookingDetail;
import swp490.spa.entities.BookingDetailStep;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConsultationContentResponse {
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
    @JsonProperty("image_before")
    private String imageBefore;
    @JsonProperty("image_after")
    private String imageAfter;
    @JsonProperty("booking_detail_step")
    @JsonIgnore
    private BookingDetailStep bookingDetailStep;
}
