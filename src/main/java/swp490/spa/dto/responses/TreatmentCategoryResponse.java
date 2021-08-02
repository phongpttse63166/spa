package swp490.spa.dto.responses;

import lombok.*;
import swp490.spa.entities.SpaService;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TreatmentCategoryResponse implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private Double totalPrice;
    private Integer totalTime;
    private List<ServiceCategoryResponse> spaServices;

}
