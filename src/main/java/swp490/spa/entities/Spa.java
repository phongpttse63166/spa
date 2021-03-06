package swp490.spa.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "spa", schema = "public")
public class Spa implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "image")
    private String image;
    @Column(name = "street")
    private String street;
    @Column(name = "district")
    private String district;
    @Column(name = "city")
    private String city;
    @Column(name = "phone")
    private String phone;
    @Column(name = "latitude")
    private String latitude;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "create_by")
    private String createBy;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "status")
    private Status status;
}
