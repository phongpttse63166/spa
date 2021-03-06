package swp490.spa.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @Column(name = "image_before", length = 65355)
    private String imageBefore;
    @Column(name = "image_after", length = 65355)
    private String imageAfter;
    @Column(name = "result", length = 65355)
    private String result;
    @Column(name = "note", length = 65355)
    private String note;
    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "booking_detail_step_id")
    private BookingDetailStep bookingDetailStep;
}
