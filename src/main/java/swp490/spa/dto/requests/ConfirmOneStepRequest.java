package swp490.spa.dto.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConfirmOneStepRequest {
    private Integer staffId;
    private Integer bookingDetailId;
}
