package com.itbangmodkradankanbanapi.DTO;


import lombok.Data;

@Data
public class TaskDTO3_V2_addTask {
    private Integer id;
    private String title;
    private String description;
    private String assignees;
    private Integer status;


    public void setDescription(String description) {
        if(description != null){
            this.description = description.trim();

            if(description.isEmpty()){
                this.description = null;
            }
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

            if(assignees.isEmpty()){
                this.assignees = null;
            }
        }


    }


}
