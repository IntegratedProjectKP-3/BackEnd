package com.itbangmodkradankanbanapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "status")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statusId")
//    @OneToMany(fetch = FetchType.EAGER,mappedBy = "task")
    private Integer statusId;
    @Column(name = "statusName")
    private String statusName;
    @Column(name = "statusDescription")
    private String statusDescription;
}
