package com.diver.apigestionfactura.pojo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NamedQueries({
        @NamedQuery(
                name = "User.findByEmail",
                query = "SELECT u FROM User u WHERE u.email = :email"
        )
})
@Data
@Entity
@DynamicUpdate // Habilita la actualization dinamica
@DynamicInsert // Habilita la insercion dinamica
@Table ( name= "users" )
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre")
    private String name;

    @Column(name = "numeroDeContacto")
    private String numeroContacto;

    @Column( name = "email")
    private  String email;

    @Column( name = "password")
    private String password;

    @Column(name = "status")
    private String status;

    @Column( name = "role")
    private String role;
}
