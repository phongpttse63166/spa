package swp490.spa.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp490.spa.entities.Role;

import javax.persistence.Column;

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
    private Role role;

    public UserResponse(Integer id, String fullname, String phone, String password, String email, String address, Role role) {
        this.id = id;
        this.fullname = fullname;
        this.phone = phone;
        this.password = password;
        this.email = email;
        this.address = address;
        this.role = role;
    }
}
