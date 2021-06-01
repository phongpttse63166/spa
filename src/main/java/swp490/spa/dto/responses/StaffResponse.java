package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swp490.spa.entities.SpaAddress;
import swp490.spa.entities.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffResponse {
    private User user;
    private SpaAddress spaAddress;
}
