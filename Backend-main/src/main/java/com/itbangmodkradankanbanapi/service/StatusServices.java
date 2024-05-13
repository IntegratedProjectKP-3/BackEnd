package com.itbangmodkradankanbanapi.service;

import com.itbangmodkradankanbanapi.entities.Status;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.repositories.StatusRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service

public class StatusServices {
    @Autowired
    private StatusRepo statusRepo;
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
    public boolean deleteStatus(Integer id){
            Status status1 = statusRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        statusRepo.delete(status1);
        return true;
    }
}
