package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp490.spa.entities.Status;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("image")
    private String image;
    @JsonProperty("street")
    private String street;
    @JsonProperty("district")
    private String district;
    @JsonProperty("city")
    private String city;
    @JsonProperty("latitude")
    private String latitude;
    @JsonProperty("longitude")
    private String longitude;
    @JsonProperty("create_by")
    private String createBy;
    @JsonProperty("create_time")
    private Date createTime;
    @JsonProperty("status")
    private Status status;

}
