package com.itbangmodkradankanbanapi.database1.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "user")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    @Id
    @Column(name = "oid")
//    @OneToMany(fetch = FetchType.EAGER,mappedBy = "task")
    private String id;

    @NotBlank
    @Column(name = "name", length = 100)
    private String name;
    @Column(name = "username",length = 50)
    private String username;
    @Column(name = "email")
    private String email;
    @Null
    @Column(name = "createdOn" , insertable = false , updatable = false)
    private Date createdOn;
    @Null
    @Column(name = "updatedOn", insertable = false , updatable = false)
    private Date updatedOn;
}
