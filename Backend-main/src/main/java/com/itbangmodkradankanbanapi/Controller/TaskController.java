package com.itbangmodkradankanbanapi.Controller;

import com.itbangmodkradankanbanapi.DTO.HomePageTaskDTO;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.service.ListMapper;
import com.itbangmodkradankanbanapi.service.TaskServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins ="http://localhost:5173")
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
    public Task getTaskById(@PathVariable Integer id)  {
        Task task = taskServices.findId(id);
//        task.setUpdatedOn(taskServices.reformatDate(task.getUpdatedOn()));
//        task.setCreatedOn(taskServices.reformatDate(task.getCreatedOn()));
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        String Create = taskServices.reformatDate(task.getCreatedOn());
//        String Update = taskServices.reformatDate(task.getUpdatedOn());
//        JSONObject responseObject = new JSONObject();
//        responseObject.put("taskId", task.getId().toString());
//        responseObject.put("taskTitle", task.getTitle() == null?"":task.getTitle().toString());
//        responseObject.put("taskDescription",task.getDesc() == null?"":task.getDesc().toString());
//        responseObject.put("taskAssignees", task.getAssignees() == null?"":task.getAssignees());
//        responseObject.put("taskStatus", task.getStatus().toString());
//        responseObject.put("CreatedOn",Create);
//        responseObject.put("UpdatedOn",Update);
//        return new ResponseEntity<>(responseObject.toString(), headers, HttpStatus.OK);
        return task;
    }
}