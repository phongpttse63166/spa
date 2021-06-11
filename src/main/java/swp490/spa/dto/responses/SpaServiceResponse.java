package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp490.spa.entities.Status;
import swp490.spa.entities.Type;

import javax.persistence.Column;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaServiceResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @Column(name = "image")
    private String image;
    @JsonProperty("description")
    private String description;
    @JsonProperty("price")
    private Double price;
    @JsonProperty("status")
    private Status status;
    @Column(name = "type")
    private Type type;
    @Column(name = "duration_min")
    private Integer durationMin;
    @JsonProperty("create_time")
    private Date createTime;
    @JsonProperty("create_by")
    private String createBy;
}
