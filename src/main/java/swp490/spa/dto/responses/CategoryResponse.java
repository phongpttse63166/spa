package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp490.spa.entities.Spa;
import swp490.spa.entities.Status;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("icon")
    private String icon;
    @JsonProperty("description")
    private String description;
    @JsonProperty("create_time")
    private Date createTime;
    @JsonProperty("create_by")
    private Integer createBy;
    @JsonProperty("status")
    private Status status;
}
