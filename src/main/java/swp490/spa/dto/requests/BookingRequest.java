package swp490.spa.dto.requests;

import lombok.*;
import swp490.spa.entities.BookingData;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingRequest {
    private Integer customerId;
    private Integer spaId;
    private List<BookingData> bookingDataList;
}
