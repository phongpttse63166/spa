package swp490.spa.dto.responses;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpaSummation {
    private Integer month;
    private Integer countServiceFinish;
}
