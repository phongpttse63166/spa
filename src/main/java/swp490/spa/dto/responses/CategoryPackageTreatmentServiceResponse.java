package swp490.spa.dto.responses;

import lombok.*;
import swp490.spa.entities.Category;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryPackageTreatmentServiceResponse implements Serializable {
    private Category category;
    private List<PackageCategoryResponse> packages;
}
