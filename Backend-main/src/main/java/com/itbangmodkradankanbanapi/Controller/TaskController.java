package com.itbangmodkradankanbanapi.Controller;

import com.itbangmodkradankanbanapi.DTO.HomePageTaskDTO;
import com.itbangmodkradankanbanapi.entities.Task;
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
@CrossOrigin(origins ="http://localhosy:5173")
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
        return task;
    }
    @PostMapping()
    public void addTask(@RequestBody List<Task> tasks) {
        List<Task> savedTasks = new ArrayList<>();
        for(Task task : tasks){
            if (task.getUpdatedOn() == null || task.getCreatedOn() == null) {
                task.setCreatedOn(new Date());
                task.setUpdatedOn(new Date());
            }
            Task task1 = taskServices.addTask(task);
            savedTasks.add(task1);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> Delete(@PathVariable Integer id){
        if (!taskServices.deleteTask(id)){
            return ResponseEntity.notFound().build();
        }
        else
            return ResponseEntity.ok().build();
    }
    
}
