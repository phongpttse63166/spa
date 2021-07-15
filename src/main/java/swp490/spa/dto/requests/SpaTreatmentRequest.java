package swp490.spa.dto.requests;

import lombok.*;
import swp490.spa.entities.Status;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpaTreatmentRequest {
    private String name;
    private String description;
    private String image;
    private Integer createBy;
    private Integer packageId;
    private List<Integer> listSpaServiceId;
}
