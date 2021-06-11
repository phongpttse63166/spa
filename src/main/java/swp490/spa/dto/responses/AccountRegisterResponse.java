package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRegisterResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("fullname")
    private String fullname;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("password")
    private String password;
    @JsonProperty("otp_code")
    private String otpCode;
    @JsonProperty("create_time")
    private Date createTime;
    @JsonProperty("expired_time")
    private Date expiredTime;
}
