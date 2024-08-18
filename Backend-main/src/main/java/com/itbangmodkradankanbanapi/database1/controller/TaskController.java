package com.itbangmodkradankanbanapi.database1.controller;

import com.itbangmodkradankanbanapi.database1.DTO.HomePageTaskDTO;
import com.itbangmodkradankanbanapi.database1.DTO.TaskDTO2;
import com.itbangmodkradankanbanapi.database1.DTO.TaskDTO3_V2;
import com.itbangmodkradankanbanapi.database1.DTO.TaskDTO3_V2_addTask;
import com.itbangmodkradankanbanapi.database1.entities.Task;
import com.itbangmodkradankanbanapi.database1.repositories.TaskRepo;
import com.itbangmodkradankanbanapi.database1.service.ListMapper;
import com.itbangmodkradankanbanapi.database1.service.TaskServices;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins ="http://ip23kp3.sit.kmutt.ac.th:3000")
//@CrossOrigin(origins ="http://localhost:5173")
@CrossOrigin(origins = {"http://intproj23.sit.kmutt.ac.th","http://ip23kp3.sit.kmutt.ac.th:3000","http://localhost:5173"})
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


    @PostMapping("")
//    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TaskDTO3_V2> addTask(@Valid @RequestBody TaskDTO3_V2_addTask task) {
            return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(taskServices.addTask(task), TaskDTO3_V2.class));
    }

    @DeleteMapping("/{id}")
    public Task Delete(@PathVariable Integer id) {
        Task task = taskServices.findId(id);
        taskServices.deleteTask(id);
        return task;
    }

    @PutMapping("/{id}")
    public  ResponseEntity<TaskDTO2> updateTask(@PathVariable Integer id ,@RequestBody TaskDTO2 taskDTO2){
        TaskDTO2 updatedTaskDTO = taskServices.updateTask(id,taskDTO2);
        return ResponseEntity.ok().body(updatedTaskDTO);
    }


}

