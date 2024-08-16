package com.itbangmodkradankanbanapi.database1.controller;

import com.itbangmodkradankanbanapi.database1.DTO.StatusDTO;
import com.itbangmodkradankanbanapi.database1.entities.Status;
import com.itbangmodkradankanbanapi.database1.repositories.StatusRepo;
import com.itbangmodkradankanbanapi.database1.service.StatusServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
//@CrossOrigin(origins ="http://ip23kp3.sit.kmutt.ac.th:3000")
//@CrossOrigin(origins ="http://localhost:5173")
@CrossOrigin(origins = {"http://intproj23.sit.kmutt.ac.th","http://ip23kp3.sit.kmutt.ac.th:3000","http://localhost:5173"})
@RequestMapping("/statuses")
public class StatusController {
    @Autowired
    private StatusServices statusServices;
    @Autowired
    private StatusRepo statusRepo;

    @GetMapping()
    public ResponseEntity<Object> getAllStatus(){
        return ResponseEntity.ok(statusServices.getAllStatus());
    }

    @PostMapping("")
    public ResponseEntity<Object> addStatus(@Valid @RequestBody StatusDTO status) {
        StatusDTO createdStatus = statusServices.addStatus(status);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }
    

    @PutMapping("/{id}")
    public  ResponseEntity<StatusDTO> updateStatus(@PathVariable Integer id , @Valid @RequestBody StatusDTO status){
        StatusDTO updatedStatus = statusServices.updateStatus(id,status);
        return ResponseEntity.ok().body(updatedStatus);
    }


    @DeleteMapping ("/{id}")
    public ResponseEntity<Object> deleteStatus(@PathVariable Integer id){
        statusServices.deleteStatus(id);
        return ResponseEntity.ok().body(new HashMap<>());
    }

    @DeleteMapping ("/{id}/{newId}")
    public ResponseEntity<Object> deleteStatus(@PathVariable Integer id , @PathVariable Integer newId){
        statusServices.deleteStatusAndTransfer(id,newId);
        return  ResponseEntity.ok().body(new HashMap<>());
    }

    @GetMapping("/{id}")
    public Status getStatusById(@PathVariable Integer id){
        return statusServices.findId(id);
    }
    }
