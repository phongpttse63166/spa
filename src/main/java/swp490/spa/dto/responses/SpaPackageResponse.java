package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp490.spa.entities.*;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpaPackageResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("image")
    private String image;
    @JsonProperty("type")
    private Type type;
    @JsonProperty("total_slot")
    private Integer totalSlot;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("create_time")
    private Date createTime;
    @JsonProperty("create_by")
    private Integer create_by;
    @JsonProperty("category_id")
    private Category category;
    @JsonProperty("spa_id")
    private Spa spa;
    @JsonProperty("services")
    private List<SpaService> spaServices;
}
