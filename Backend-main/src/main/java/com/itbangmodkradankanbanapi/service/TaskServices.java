package com.itbangmodkradankanbanapi.service;

import com.itbangmodkradankanbanapi.DTO.HomePageTaskDTO;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.repositories.TaskRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public String reformatDate(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String afterFormat = dateFormat.format(date);
        return afterFormat;
    }

}