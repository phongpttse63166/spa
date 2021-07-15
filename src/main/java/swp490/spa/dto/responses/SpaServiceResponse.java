package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp490.spa.entities.*;

import java.sql.Date;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaServiceResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("image")
    private String image;
    @JsonProperty("description")
    private String description;
    @JsonProperty("price")
    private Double price;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("type")
    private Type type;
    @JsonProperty("duration_min")
    private Integer durationMin;
    @JsonProperty("create_time")
    private Date createTime;
    @JsonProperty("create_by")
    private String createBy;
}
