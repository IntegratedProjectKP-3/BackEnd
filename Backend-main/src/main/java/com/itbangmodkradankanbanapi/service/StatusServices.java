package com.itbangmodkradankanbanapi.service;

import com.itbangmodkradankanbanapi.entities.Status;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.repositories.StatusRepo;
import com.itbangmodkradankanbanapi.repositories.TaskRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service

public class StatusServices {
    @Autowired
    private StatusRepo statusRepo;
    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;
    public Status AddStatus(Status status){
        Status status1 = new Status(status.getStatusId(),status.getStatusName(),status.getStatusDescription());
        return statusRepo.saveAndFlush(status1);
    }
    public Status findId(Integer Id){
        return statusRepo.findById(Id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"status "+Id + " does not exist !!!"));
    }
    public List<Status> getAllStatus(){
        return statusRepo.findAll();
    }
    public boolean updateStatus(Status status){
        Status status1 = statusRepo.findById(status.getStatusId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        status1.setStatusId(status.getStatusId());
        status1.setStatusName(status.getStatusName());
        status1.setStatusDescription(status.getStatusDescription());
        statusRepo.saveAndFlush(statusRepo.save(status1));
        return true;
    }
    public boolean deleteStatus(Integer id) {
        Status status = statusRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<Task> tasks = taskRepo.findAll();

        for (Task task : tasks) {
            if (Objects.equals(status.getStatusId(), task.getStatus().getStatusId())) {
                Status defaultStatus = statusRepo.findById(1).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
                task.setStatus(defaultStatus);
                taskRepo.save(task);
            }
        }
        statusRepo.delete(status);
        return true;
    }
}
