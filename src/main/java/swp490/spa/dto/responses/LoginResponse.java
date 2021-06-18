package swp490.spa.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LoginResponse {
    private boolean isValid;
    private String jsonWebToken;
    private String errorMessage;
    private Integer errorCode;
    private String role;
    private int idAccount;
    private Integer spaId;


    public static LoginResponse createErrorResponse(Error error){
        return new LoginResponse(false, null,
                error.getMessage(), error.getCode(), null, 0, 0);
    }

    public static LoginResponse createSuccessResponse(String jsonWebToken, String role, int idAccount, Integer spaId){
        return new LoginResponse(true,jsonWebToken, null, null, role, idAccount, spaId);
    }

    public enum Error{
        USERNAME_NOT_FOUND(1,"Số điện thoại không tồn tại"),
        WRONG_PASSWORD(2,"Sai mật khẩu, vui lòng kiểm tra lại"),
        ROLE_NOT_EXISTED(3,"Role này không tồn tại"),
        BLANK_FIELD(4,"Không đầy đủ field"),
        CUSTOMER_NOT_EXISTED(5, "Customer không tồn tại"),
        MANAGER_NOT_EXISTED(5, "Manager không tồn tại"),
        ADMIN_NOT_EXISTED(5, "Admin không tồn tại"),
        CONSULTANT_NOT_EXISTED(5, "Consultant không tồn tại"),
        STAFF_NOT_EXISTED(5, "Staff không tồn tại");

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
