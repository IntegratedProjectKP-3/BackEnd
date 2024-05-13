package com.itbangmodkradankanbanapi.DTO;

import com.itbangmodkradankanbanapi.entities.Status;
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