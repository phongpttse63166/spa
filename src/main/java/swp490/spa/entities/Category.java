package swp490.spa.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
@Table(name = "category", schema = "public")
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "icon")
    private String icon;
    @Column(name = "description", length = 65355)
    private String description;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "create_by")
    private Integer createBy;
    @Column(name = "status")
    private Status status;
    @ManyToOne
    @JoinColumn(name = "spa_id")
    private Spa spa;
}
