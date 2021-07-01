package swp490.spa.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "consultation_content", schema = "public")
public class ConsultationContent implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "description", length = 65355)
    private String description;
    @Column(name = "expectation", length = 65355)
    private String expectation;
    @Column(name = "result", length = 65355)
    private String result;
    @Column(name = "note", length = 65355)
    private String note;
    @OneToOne
    @JoinColumn(name = "booking_detail_step_id")
    private BookingDetailStep bookingDetailStep;
}
