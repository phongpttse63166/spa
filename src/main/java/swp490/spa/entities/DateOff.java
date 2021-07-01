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
public class DateOff  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "date_off")
    private Date dateOff;
    @Column(name = "status_date_off")
    private StatusDateOff statusDateOff;
    @Column(name = "reason_date_off", length = 65355)
    private String reasonDateOff;
    @Column(name = "reason_cancel", length = 65355)
    private String reasonCancel;
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;
    @ManyToOne
    @JoinColumn(name = "spa_id")
    private Spa spa;
}
