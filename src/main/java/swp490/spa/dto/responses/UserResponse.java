package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private Integer id;
    private String fullname;
    private String phone;
    private String password;
    private String email;
    private String address;

    public UserResponse(Integer id, String fullname, String phone, String password, String email, String address) {
        this.id = id;
        this.fullname = fullname;
        this.phone = phone;
        this.password = password;
        this.email = email;
        this.address = address;
    }
}
