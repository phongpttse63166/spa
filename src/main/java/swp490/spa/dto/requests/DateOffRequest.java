package swp490.spa.dto.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DateOffRequest {
    private String dateOff;
    private String reasonDateOff;
}
