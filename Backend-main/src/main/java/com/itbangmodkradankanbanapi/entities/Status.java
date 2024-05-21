package com.itbangmodkradankanbanapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "statusId")
////    @OneToMany(fetch = FetchType.EAGER,mappedBy = "task")
//    private Integer statusId;
//
//    @NotBlank
//    @Column(name = "statusName")
//    private String statusName;
//
//    @Column(name = "statusDescription")
//    private String statusDescription;
//
//    @JsonIgnore
//    @OneToMany(mappedBy = "status")
//    private List<Task> taskList ;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statusId")
//    @OneToMany(fetch = FetchType.EAGER,mappedBy = "task")
    private Integer id;

    @NotBlank
    @Column(name = "statusName", length = 50)
    private String name;

    @Column(name = "statusDescription")
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "status")
    private List<Task> taskList ;
}
