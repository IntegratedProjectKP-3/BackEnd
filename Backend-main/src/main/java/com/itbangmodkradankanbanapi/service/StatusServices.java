package com.itbangmodkradankanbanapi.service;

import com.itbangmodkradankanbanapi.DTO.StatusDTO;
import com.itbangmodkradankanbanapi.entities.Status;
import com.itbangmodkradankanbanapi.entities.Task;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import com.itbangmodkradankanbanapi.repositories.StatusRepo;
import com.itbangmodkradankanbanapi.repositories.TaskRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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

    public Status findId(Integer Id){
        return statusRepo.findById(Id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"status "+Id + " does not exist !!!"));
    }

    public List<Status> getAllStatus(){
        return statusRepo.findAll();
    }


    @Transactional
    public StatusDTO addStatus(StatusDTO newStatus) {
        List<Status> statusList = statusRepo.findAllByNameIgnoreCase(newStatus.getName());
        if(!statusList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not duplicate name");
        }

        Status status = modelMapper.map(newStatus, Status.class);
        status.setName(newStatus.getName().trim());
        Status updatedStatus = statusRepo.saveAndFlush(status);
        return modelMapper.map(updatedStatus, StatusDTO.class);
    }

    @Transactional
    public StatusDTO updateStatus(Integer id, StatusDTO status) {
        if(id == 1){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID cannot be 1");
        }
        Status currentStatus = statusRepo.findById(id).orElseThrow(
                () -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        currentStatus.setName(status.getName());
        currentStatus.setDescription(status.getDescription());
        Status updateStatus = statusRepo.save(currentStatus);
        return modelMapper.map(updateStatus ,StatusDTO.class);
    }

    @Transactional
    public void deleteStatus(Integer id) {
        System.out.println(id);
        Status status = statusRepo.findById(id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));

        statusRepo.delete(status);
    }

    @Transactional
    public void deleteStatusAndTransfer(Integer id, Integer newId) {
        Status status = statusRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        Status transferStatus = statusRepo.findById(newId)
                .orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));

        List<Task> taskV2s = status.getTaskList();
        taskV2s.forEach(task -> task.setStatus(transferStatus));
        taskRepo.saveAll(taskV2s);
        statusRepo.delete(status);
    }

}
