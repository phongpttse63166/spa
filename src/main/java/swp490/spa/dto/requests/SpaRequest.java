package swp490.spa.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaRequest {
    private String name;
    private String street;
    private String district;
    private String longitude;
    private String latitude;
    private String city;
    private Integer adminId;
    private MultipartFile file;
}
