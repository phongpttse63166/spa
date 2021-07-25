package swp490.spa.dto.requests;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ManagerRequest implements Serializable {
    private String fullname;
    private String phone;
    private String gender;
    private String birthdate;
    private String email;
    private MultipartFile file;
    private String address;
    private Integer spaId;
}
