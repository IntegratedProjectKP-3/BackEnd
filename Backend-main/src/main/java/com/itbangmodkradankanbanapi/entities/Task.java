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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // สร้างคีย์หลักโดยอัตโนมัติ
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
    private String status = "NO_STATUS";

    @Column(name = "createdOn" , insertable = false , updatable = false)
    private Date createdOn;
    @Column(name = "updatedOn", insertable = false , updatable = false)
    private Date updatedOn;

    public void setStatus(String status) {
        if(status == null){
            this.status = "NO_STATUS";
        }else{
            this.status = status;
        }

    }
}