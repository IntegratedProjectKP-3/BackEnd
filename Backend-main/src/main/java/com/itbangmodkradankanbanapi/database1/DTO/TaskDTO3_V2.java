package com.itbangmodkradankanbanapi.database1.DTO;


import com.itbangmodkradankanbanapi.database1.entities.Status;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class TaskDTO3_V2 {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private Status status;
    public void setDescription(String description) {
        if(description != null){
            this.description = description.trim();
        }
    }

    public void setTitle(String title) {
        if(title != null){
            this.title = title.trim();
        }
    }

    public void setAssignees(String assignees) {
        if(assignees != null){
            this.assignees = assignees.trim();
        }

    }


}
