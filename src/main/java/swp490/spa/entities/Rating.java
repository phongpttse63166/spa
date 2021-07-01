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
@Table(name = "rating", schema = "public")
public class Rating implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "rate")
    private Double rate;
    @Column(name = "comment", length = 65355)
    private String comment;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "expired_time")
    private Date expireTime;
    @Column(name = "status_rating")
    private StatusRating statusRating;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @OneToOne
    @JoinColumn(name = "booking_detail_step_id")
    private BookingDetailStep bookingDetailStep;
}
