package com.itbangmodkradankanbanapi.database1.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "invite")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Invite {
    @Id
    @Column(name = "idinvite",length = 10)
    private String id;
    @Column(name = "name")
    private String name;
    @Column(nullable = false, name = "email")
    private String email;
    @Column(name = "access-right")
    private String access;
    @Column(name = "boardId",length = 10)
    private String boardId;
    @Column(name = "add_on",insertable = false , updatable = false)
    private Date addOn;
    @Column(name = "oid")
    private String oid;
}