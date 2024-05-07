package com.itbangmodkradankanbanapi.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "task")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY) // สร้างคีย์หลักโดยอัตโนมัติ
    @Column(name = "taskId")
    private Integer id;
    @NonNull
    @Column(name = "taskTitle")
    private String title;
    @Column(name = "taskDescription")
    private String description;
    @Column(name = "taskAssignees")
    private String assignees;
    @NonNull
    @Column(name = "taskStatus")
    private String status;
    @Column(name = "createdOn")
    private Date createdOn;
    @Column(name = "updatedOn")
    private Date updatedOn;
}