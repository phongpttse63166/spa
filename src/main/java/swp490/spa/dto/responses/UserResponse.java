package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("fullname")
    private String fullname;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("password")
    private String password;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("birthdate")
    private String birthdate;
    @JsonProperty("email")
    private String email;
    @JsonProperty("image")
    private String image;
    @JsonProperty("address")
    private String address;
    @JsonProperty("isActive")
    private boolean isActive;
}
