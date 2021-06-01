package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp490.spa.entities.Status;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpaResponse {
    private Integer id;
    private String name;
    private String image;
    private String createBy;
    private String createTime;
    private Status status;

    public SpaResponse(Integer id, String name, String image, String createBy,
                       String createTime, Status status) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.createBy = createBy;
        this.createTime = createTime;
        this.status = status;
    }
}
