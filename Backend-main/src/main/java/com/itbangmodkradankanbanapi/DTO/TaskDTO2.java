package com.itbangmodkradankanbanapi.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO2 {
    private Integer id;
    private String title;
    private String assignees;
    private String description;
    private String status = "NO_STATUS";

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


    public void setStatus(String status) {
        if(status == null){
            this.status = "NO_STATUS";
        }else{
            this.status = status;
        }

    }
}
