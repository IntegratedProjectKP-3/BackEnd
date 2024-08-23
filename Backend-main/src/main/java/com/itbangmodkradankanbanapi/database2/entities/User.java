package com.itbangmodkradankanbanapi.database2.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "users", schema = "itbkk_shared")
public class User {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oid")
    private String oid;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Roles role;


    public enum Roles{
        LECTURER,
        STAFF,
        STUDENT
    }
}
