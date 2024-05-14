package com.itbangmodkradankanbanapi.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "task")
public class HomePageTaskDTO {
    @Id
//    @JsonIgnore
    @Column(name = "taskId")
    private Integer id;
    @Column(name = "taskTitle")
    private String title;
    @Column(name = "taskAssignees")
    private String assignees;
    @Column(name = "taskStatus")
    private String status;
}