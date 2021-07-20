package swp490.spa.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DateOffByDate implements Serializable {
    private Date dateOff;
    private List<DateOff> dateOffList;
}
