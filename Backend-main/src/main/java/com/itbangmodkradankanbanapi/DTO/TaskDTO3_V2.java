package com.itbangmodkradankanbanapi.DTO;


import com.itbangmodkradankanbanapi.entities.Status;
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
