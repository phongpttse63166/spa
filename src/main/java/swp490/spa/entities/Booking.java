package swp490.spa.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

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
    @Column(name = "total_slot")
    private Integer totalSlot;
    @Column(name = "status_booking")
    private StatusBooking statusBooking;
    @Column(name = "date_booking")
    private Date dateBooking;
    @Column(name = "create_time")
    private Date createTime;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "consultant_id")
    private Consultant consultant;
    @ManyToOne
    @JoinColumn(name = "spa_id")
    private Spa spa;
}
