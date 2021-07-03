package swp490.spa.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "booking", schema = "public")
public class Booking implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "total_price")
    private Double totalPrice;
    @Column(name = "total_time")
    private Integer totalTime;
    @Column(name = "status_booking")
    private StatusBooking statusBooking;
    @Column(name = "create_time")
    private Date createTime;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "spa_id")
    private Spa spa;
    @OneToMany(cascade = CascadeType.ALL)
    @JsonBackReference
    private List<BookingDetail> bookingDetails = new ArrayList<>();
}
