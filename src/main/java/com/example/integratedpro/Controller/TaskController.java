package com.example.integratedpro.Controller;

import com.example.integratedpro.DTO.HomePageTaskDTO;
import com.example.integratedpro.entities.Task;
import com.example.integratedpro.services.ListMapper;
import com.example.integratedpro.services.TaskServices;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskServices taskServices;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;

    @GetMapping
    public List<HomePageTaskDTO> getAllTask(){
        return taskServices.getAllTask();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> getTaskById(@PathVariable Integer id) throws JSONException {
        Task task = taskServices.findId(id);
//        List<HomePageTaskDTO> dto = taskServices.findId(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String Create = taskServices.reformatDate(task.getCreatedOn());
        String Update = taskServices.reformatDate(task.getUpdatedOn());
        JSONObject responseObject = new JSONObject();
//        responseObject.put("dto", dto.toString());
        responseObject.put("taskId", task.getId().toString());
        responseObject.put("taskTitle", task.getTitle() == null?"":task.getTitle().toString());
        responseObject.put("taskDescription",task.getDesc() == null?"":task.getDesc().toString());
        responseObject.put("taskAssignees", task.getAssignees() == null?"":task.getAssignees());
        responseObject.put("taskStatus", task.getStatus().toString());
        responseObject.put("CreatedNo",Create);
        responseObject.put("UpdatedNo",Update);
        return new ResponseEntity<>(responseObject.toString(), headers, HttpStatus.OK);
    }
}
