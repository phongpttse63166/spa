package swp490.spa.dto.responses;

import lombok.*;
import swp490.spa.entities.Status;
import swp490.spa.entities.Type;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PackageCategoryResponse implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private String image;
    private Type type;
    private Status status;
    private List<TreatmentCategoryResponse> treatments;
}
