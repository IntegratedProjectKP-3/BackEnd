package com.itbangmodkradankanbanapi.database1.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taskId")
    private Integer id;

    @Column(nullable = false, name = "taskTitle")
    private String title;

    @Column(name = "taskDescription")
    private String description;
    @Column(name = "taskAssignees")
    private String assignees;

    @Column(name = "createdOn" , insertable = false , updatable = false)
    private Date createdOn;
    @Column(name = "updatedOn", insertable = false , updatable = false)
    private Date updatedOn;

    @ManyToOne
    @JoinColumn(name = "taskStatus")
    private Status status;



}