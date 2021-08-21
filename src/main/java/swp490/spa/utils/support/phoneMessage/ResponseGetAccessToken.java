package swp490.spa.utils.support.phoneMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseGetAccessToken {
    private String access_token;
    private String expires_in;
}

