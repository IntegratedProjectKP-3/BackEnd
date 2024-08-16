package com.itbangmodkradankanbanapi.database1.DTO;

import com.itbangmodkradankanbanapi.database1.entities.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO2 {

    private Integer id;
    private String title;
    private String assignees;
    private String description;
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
