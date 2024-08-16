package com.itbangmodkradankanbanapi.database1.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itbangmodkradankanbanapi.database1.entities.Status;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status")
    private Status status;

}