package com.itbangmodkradankanbanapi.Controller;

import com.itbangmodkradankanbanapi.DTO.HomePageTaskDTO;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.repositories.TaskRepo;
import com.itbangmodkradankanbanapi.service.ListMapper;
import com.itbangmodkradankanbanapi.service.TaskServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
//@CrossOrigin(origins ="http://ip23kp3.sit.kmutt.ac.th:3000")
@CrossOrigin(origins ="http://localhost:5173")
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskServices taskServices;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;
    @Autowired
    private TaskRepo taskRepo;

    @GetMapping
    public List<HomePageTaskDTO> getAllTask(){
        return taskServices.getAllTask();
    }
    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Integer id)  {
        return taskServices.findId(id);
    }
    @PostMapping()
    public List<Task> addTask(@RequestBody List<Task> tasks) {
        for(Task task : tasks){
            if (task.getUpdatedOn() == null || task.getCreatedOn() == null) {
                task.setCreatedOn(new Date());
                task.setUpdatedOn(new Date());
            }
           taskServices.addTask(task);
        }
        return tasks;
    }
    @DeleteMapping("/{id}")
    public Task Delete(@PathVariable Integer id) {
        Task task = taskServices.findId(id);
        taskServices.deleteTask(id);
        return task;
    }
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Integer id, @RequestBody List<Task> tasks) {
        Task task1 = taskServices.findId(id);
            for(Task task : tasks){
                taskServices.updateTask(task);
            }
            return task1;
        }
    }

