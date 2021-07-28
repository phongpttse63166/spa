package swp490.spa.dto.requests;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConfirmAStepRequest implements Serializable {
    private Integer staffId;
    private Integer bookingDetailStepId;
    private String result;
}
