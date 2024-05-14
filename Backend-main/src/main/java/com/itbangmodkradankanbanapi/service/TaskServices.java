package com.itbangmodkradankanbanapi.service;

import com.itbangmodkradankanbanapi.DTO.HomePageTaskDTO;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.repositories.TaskRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    public Task addTask(Task task){
        return taskRepo.save(task);
    }
    public boolean updateTask(Task task){
        Task task1 = taskRepo.findById(task.getId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"TaskId "+task.getId() + " does not exist !!!"));
        task1.setTitle(task.getTitle());
        task1.setDescription(task.getDescription());
        task1.setAssignees(task.getAssignees());
        task1.setStatus(task.getStatus());
        if (task1.getUpdatedOn() == null){
            task1.setUpdatedOn(new Date());
        }else{
            task1.setUpdatedOn(task1.getUpdatedOn());
        }
        taskRepo.saveAndFlush(taskRepo.save(task1));
        return true;
    }
}