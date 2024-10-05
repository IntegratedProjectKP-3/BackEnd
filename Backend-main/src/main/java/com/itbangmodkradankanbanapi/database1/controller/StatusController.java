package com.itbangmodkradankanbanapi.database1.controller;

import com.itbangmodkradankanbanapi.database1.DTO.StatusDTO;
import com.itbangmodkradankanbanapi.database1.DTO.TaskDTO3_V2;
import com.itbangmodkradankanbanapi.database1.entities.Status;
import com.itbangmodkradankanbanapi.database1.repositories.StatusRepo;
import com.itbangmodkradankanbanapi.database1.service.StatusServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
//@CrossOrigin(origins ="http://ip23kp3.sit.kmutt.ac.th:3000")
//@CrossOrigin(origins ="http://localhost:5173")
@CrossOrigin(origins = {"http://intproj23.sit.kmutt.ac.th","http://ip23kp3.sit.kmutt.ac.th:3000","http://localhost:5173"})
@RequestMapping("/boards")
public class StatusController {
    @Autowired
    private StatusServices statusServices;
    @Autowired
    private StatusRepo statusRepo;

    @GetMapping("/statuses")
    public List<StatusDTO> getAllStatus(){

        return statusServices.getAllStatus();
    }
    @GetMapping("/{boardId}/statuses")
    public ResponseEntity<?> getAllPrivateStatus(@PathVariable String boardId, @RequestHeader("Authorization") String token){
        List<StatusDTO> newTask = statusServices.findPrivateTask(token, boardId);
        if (newTask != null) {
            return ResponseEntity.status(HttpStatus.OK).body(newTask);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT FOUND");
        }
    }
    @PostMapping("/{boardId}/statuses")
    public ResponseEntity<Object> addStatus(@Valid @RequestBody StatusDTO status,@PathVariable String boardId, @RequestHeader("Authorization") String token) {
        StatusDTO createdStatus = statusServices.addStatus(status,token,boardId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }
    @PutMapping("/{boardId}/statuses/{id}")
    public ResponseEntity<Object> UpdatePrivateStatus(@PathVariable String boardId, @PathVariable Integer id,@RequestHeader("Authorization") String token,@Valid @RequestBody StatusDTO statusDTO){
        StatusDTO updateStatus = statusServices.updateStatus(id,statusDTO,token,boardId);
        if (updateStatus != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updateStatus);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT FOUND");
        }
    }
    @DeleteMapping("/{boardId}/statuses/{id}")
    public ResponseEntity<?> deleteStatus(@PathVariable String boardId,@PathVariable Integer id,@RequestHeader("Authorization") String token){
        statusServices.deleteStatus(id, token, boardId);
        return ResponseEntity.ok().body(new HashMap<>());
    }
    @DeleteMapping("/{boardId}/statuses/{id}/{newId}")
    public ResponseEntity<?> deleteAndTransferStatus(@PathVariable String boardId,@PathVariable Integer id,@PathVariable Integer newId,@RequestHeader("Authorization") String token){
        statusServices.deleteStatusAndTransfer(id,newId, boardId, token);
        return ResponseEntity.ok().body(new HashMap<>());
    }
//    @PostMapping("")
//    public ResponseEntity<Object> addStatus(@Valid @RequestBody StatusDTO status) {
//        StatusDTO createdStatus = statusServices.addStatus(status,);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
//    }
//
//
//    @PutMapping("/{id}")
//    public  ResponseEntity<StatusDTO> updateStatus(@PathVariable Integer id , @Valid @RequestBody StatusDTO status){
//        StatusDTO updatedStatus = statusServices.updateStatus(id,status);
//        return ResponseEntity.ok().body(updatedStatus);
//    }
//
//
//    @DeleteMapping ("/{id}")
//    public ResponseEntity<Object> deleteStatus(@PathVariable Integer id){
//        statusServices.deleteStatus(id);
//        return ResponseEntity.ok().body(new HashMap<>());
//    }
//
//    @DeleteMapping ("/{id}/{newId}")
//    public ResponseEntity<Object> deleteStatus(@PathVariable Integer id , @PathVariable Integer newId){
//        statusServices.deleteStatusAndTransfer(id,newId);
//        return  ResponseEntity.ok().body(new HashMap<>());
//    }
//
//    @GetMapping("/{id}")
//    public Status getStatusById(@PathVariable Integer id){
//        return statusServices.findId(id);
//    }
    }
