package com.example.integratedpro.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "task")
@Getter
@Setter
public class Task {
    @Id
    @NonNull
    @Column(name = "taskId")
    private Integer Id;
    @NonNull
    @Column(name = "taskTitle")
    private String title;
    @Column(name = "taskDescription")
    private String Desc;
    @Column(name = "taskAssignees")
    private String Assignees;
    @NonNull
    @Column(name = "taskStatus")
    private String Status;
    @NonNull
    @Column(name = "createdOn")
    private Date createdOn;
    @NonNull
    @Column(name = "updatedOn")
    private Date updatedOn;
}
