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
@Table(name = "booking_detail", schema = "public")
public class BookingDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "total_slot")
    private Integer totalSlot;
    @Column(name = "type")
    private Type type;
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
    @ManyToOne
    @JoinColumn(name = "treatment_id")
    private SpaTreatment spaTreatment;
    @ManyToOne
    @JoinColumn(name = "spa_package_id")
    private SpaPackage spaPackage;
}
