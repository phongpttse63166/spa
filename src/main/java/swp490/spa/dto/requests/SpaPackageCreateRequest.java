package swp490.spa.dto.requests;

import lombok.*;
import swp490.spa.entities.Status;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpaPackageCreateRequest {
    private String name;
    private String description;
    private String image;
    private Status status;
    private Date createTime;
    private Integer createBy;
    private Integer categoryId;
    private Integer spaId;
    private List<Integer> listSpaServiceId;
}
