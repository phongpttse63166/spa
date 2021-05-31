package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private boolean isValid;
    private String jsonWebToken;
    private String errorMessage;
    private Integer errorCode;
    private String role;
    private int UserId;


    public static LoginResponse createErrorResponse(Error error){
        return new LoginResponse(false, null, error.getMessage(), error.getCode(), null, 0);
    }

    public static LoginResponse createSuccessResponse(String jsonWebToken, String role, int UserId){
        return new LoginResponse(true,jsonWebToken, null, null, role, UserId);
    }

    public enum Error{
        USERNAME_NOT_FOUND(1,"Your phone number is not existed!"),
        WRONG_PASSWORD(2,"Wrong password! Please check!");

        private final int code;
        private final String message;

        Error(int code, String message){
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
