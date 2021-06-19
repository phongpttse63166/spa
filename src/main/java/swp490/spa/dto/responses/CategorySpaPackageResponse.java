package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp490.spa.entities.Category;
import swp490.spa.entities.SpaPackage;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategorySpaPackageResponse {
    @JsonProperty("category")
    private Category category;
    @JsonProperty("spapackages")
    private List<SpaPackage> spaPackages;
}
