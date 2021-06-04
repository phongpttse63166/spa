package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp490.spa.entities.Spa;
import swp490.spa.entities.SpaPackage;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpaTreatmentResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("create_time")
    private Date createTime;
    @JsonProperty("create_by")
    private Integer createBy;
    @JsonProperty("spa_package")
    private SpaPackage spaPackage;
    @JsonProperty("spa")
    private Spa spa;
}
