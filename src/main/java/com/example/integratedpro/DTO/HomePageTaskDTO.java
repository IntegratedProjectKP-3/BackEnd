package com.example.integratedpro.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NonNull;

@Entity
@Data
@Table(name = "task")
public class HomePageTaskDTO {
    @Id
    @JsonIgnore
    @Column(name = "taskId")
    private Integer Id;
    @Column(name = "taskTitle")
    private String title;
    @Column(name = "taskAssignees")
    private String Assignees;
    @Column(name = "taskStatus")
    private String Status;
}
