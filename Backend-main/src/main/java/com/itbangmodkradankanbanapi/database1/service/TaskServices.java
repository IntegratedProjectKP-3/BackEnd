package com.itbangmodkradankanbanapi.database1.service;

import com.itbangmodkradankanbanapi.database1.DTO.HomePageTaskDTO;
import com.itbangmodkradankanbanapi.database1.DTO.TaskDTO2;
import com.itbangmodkradankanbanapi.database1.DTO.TaskDTO3_V2;
import com.itbangmodkradankanbanapi.database1.DTO.TaskDTO3_V2_addTask;
import com.itbangmodkradankanbanapi.database1.entities.Status;
import com.itbangmodkradankanbanapi.database1.entities.Task;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import com.itbangmodkradankanbanapi.database1.repositories.StatusRepo;
import com.itbangmodkradankanbanapi.database1.repositories.TaskRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TaskServices {
    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    private StatusRepo statusRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    public List<HomePageTaskDTO> getAllTask(){
        return listMapper.mapList(taskRepo.findAll(),HomePageTaskDTO.class,modelMapper);
    }
    public Task findId(Integer Id){
        return taskRepo.findById(Id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"TaskId "+Id + " does not exist !!!"));
    }

    @Transactional
    public TaskDTO3_V2 addTask(TaskDTO3_V2_addTask newTask){
        Status statusObject = statusRepo.findById(newTask.getStatus()).orElseThrow(()-> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        Task task = modelMapper.map(newTask, Task.class);
        task.setStatus(statusObject);
        Task savedTask = taskRepo.saveAndFlush(task);
        return modelMapper.map(savedTask, TaskDTO3_V2.class);
    }


//    public boolean deleteTask(Integer Id){
//        try {
//            taskRepo.deleteById(Id);
//            return true;
//        }catch (EmptyResultDataAccessException e) {
//            return false;
//        }
//    }

    @Transactional
    public TaskDTO2 deleteTask(int id){
        Task task =  taskRepo.findById(id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        TaskDTO2 taskDTO = modelMapper.map(task, TaskDTO2.class);
        taskRepo.delete(task);
        taskDTO.setDescription(null);
        return taskDTO;
    }

    @Transactional
    public TaskDTO2 updateTask(Integer id, TaskDTO2 taskDTO2) {
        Task existingTask = taskRepo.findById(id).orElseThrow(
                () -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        existingTask.setDescription(taskDTO2.getDescription());
        existingTask.setTitle(taskDTO2.getTitle());
        existingTask.setAssignees(taskDTO2.getAssignees());
        existingTask.setStatus(taskDTO2.getStatus());
        Task savedTask = taskRepo.save(existingTask);
        TaskDTO2 updateTaskDTO = modelMapper.map(savedTask, TaskDTO2.class);
        updateTaskDTO.setDescription(null);
        return  updateTaskDTO;
    }


}