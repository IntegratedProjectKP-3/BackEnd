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

    @Column(name = "created_on" , insertable = false , updatable = false)
    private Date createdOn;
    @Column(name = "updated_on", insertable = false , updatable = false)
    private Date updatedOn;
    @Column(name = "board_Id")
    private String boardId;

    @ManyToOne
    @JoinColumn(name = "taskStatus")
    private Status status;



}