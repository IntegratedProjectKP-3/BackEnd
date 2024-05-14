package com.itbangmodkradankanbanapi.service;

import com.itbangmodkradankanbanapi.DTO.HomePageTaskDTO;
import com.itbangmodkradankanbanapi.DTO.TaskDTO2;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import com.itbangmodkradankanbanapi.repositories.TaskRepo;
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
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;

    public List<HomePageTaskDTO> getAllTask(){
        return listMapper.mapList(taskRepo.findAll(),HomePageTaskDTO.class,modelMapper);
    }
    public Task findId(Integer Id){
        return taskRepo.findById(Id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"TaskId "+Id + " does not exist !!!"));
    }
    public boolean deleteTask(Integer Id){
        try {
            taskRepo.deleteById(Id);
            return true;
        }catch (EmptyResultDataAccessException e) {
            return false;
        }
    }


    @Transactional
    public TaskDTO2 addTask(TaskDTO2 newTask) {
        Task task = modelMapper.map(newTask, Task.class);
        Task updatedTask = taskRepo.saveAndFlush(task);
        return modelMapper.map(updatedTask, TaskDTO2.class);
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