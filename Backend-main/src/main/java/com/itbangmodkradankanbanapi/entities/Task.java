package com.itbangmodkradankanbanapi.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.util.Date;
import java.util.List;

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

//    @NonNull
//    @Column(name = "taskStatus")
//    private Integer status;

    @Column(name = "createdOn")
    private Date createdOn;
    @Column(name = "updatedOn")
    private Date updatedOn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "taskStatus",referencedColumnName = "statusId")
    private Status  status;
//    @Formula("(SELECT statusName FROM status s WHERE s.statusId = taskStatus)")
//    private String statusName;

}