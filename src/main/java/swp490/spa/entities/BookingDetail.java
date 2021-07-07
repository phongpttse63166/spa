package swp490.spa.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    @Column(name = "total_time")
    private Integer totalTime;
    @Column(name = "type")
    private Type type;
    @Column(name = "total_price")
    private Double totalPrice;
    @Column(name = "status_booking")
    private StatusBooking statusBooking;
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
    @ManyToOne
    @JoinColumn(name = "treatment_id")
    private SpaTreatment spaTreatment;
    @ManyToOne
    @JoinColumn(name = "spa_package_id")
    private SpaPackage spaPackage;
    @OneToMany(cascade = CascadeType.ALL)
    @JsonBackReference
    private List<BookingDetailStep> bookingDetailSteps = new ArrayList<>();

    public void addAllBookingDetailStep(List<BookingDetailStep> bookingDetailStepList){
        this.bookingDetailSteps.addAll(bookingDetailStepList);
    }
}
