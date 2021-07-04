package swp490.spa.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingData implements Serializable {
    private Integer packageId;
    private Date dateBooking;
    private Time timeBooking;
    private Time endTimeBooking;
}
