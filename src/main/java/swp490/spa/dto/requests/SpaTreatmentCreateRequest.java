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
public class SpaTreatmentCreateRequest {
    private String name;
    private String description;
    private String image;
    private Integer createBy;
    private Integer packageId;
    private Integer spaId;
    private List<Integer> listSpaServiceId;
}
