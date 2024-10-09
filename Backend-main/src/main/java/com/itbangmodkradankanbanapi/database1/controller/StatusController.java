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
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
//@CrossOrigin(origins ="http://ip23kp3.sit.kmutt.ac.th:3000")
//@CrossOrigin(origins ="http://localhost:5173")
@CrossOrigin(origins = {"http://ip23kp3.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th","http://ip23kp3.sit.kmutt.ac.th:3000","http://localhost:5173"})
@RequestMapping("/boards")
public class StatusController {
    @Autowired
    private StatusServices statusServices;
    @Autowired
    private StatusRepo statusRepo;

    @GetMapping("/statuses")
    public List<StatusDTO> getAllStatus() {
        return statusServices.getAllStatus();
    }

    @GetMapping("/{boardId}/statuses")
    public ResponseEntity<?> getAllPrivateStatus(@PathVariable String boardId, @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Forbidden: No token provided.");
        }
        Object newTask = statusServices.findPrivateStatus(token, boardId);
        if (newTask instanceof List<?>) {
            return ResponseEntity.status(HttpStatus.OK).body(newTask);
        } else if(newTask.equals("403")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(newTask);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT FOUND");
        }
    }

    @PostMapping("/{boardId}/statuses")
    public ResponseEntity<Object> addStatus(@Valid @RequestBody StatusDTO status, @PathVariable String boardId,  @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED: No token provided.");
        }
        StatusDTO createdStatus = statusServices.addStatus(status, token, boardId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
    }

    @PutMapping("/{boardId}/statuses/{id}")
    public ResponseEntity<Object> UpdatePrivateStatus(@PathVariable String boardId, @PathVariable Integer id,  @RequestHeader(value = "Authorization", required = false) String token, @Valid @RequestBody StatusDTO statusDTO) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED: No token provided.");
        }
        StatusDTO updateStatus = statusServices.updateStatus(id, statusDTO, token, boardId);
        if (updateStatus != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updateStatus);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT FOUND");
        }
    }

    @DeleteMapping("/{boardId}/statuses/{id}")
    public ResponseEntity<?> deleteStatus(@PathVariable String boardId, @PathVariable Integer id,  @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED: No token provided.");
        }
        try{
            Object status = statusServices.deleteStatus(id, token, boardId);
            if(status.equals("deleted")){
                return ResponseEntity.ok().body(new HashMap<>());
            }else if (status.equals("You do not have permission to delete")){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(status);
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(status);
            }
        }catch (ResponseStatusException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("not found");
        }
    }

    @DeleteMapping("/{boardId}/statuses/{id}/{newId}")
    public ResponseEntity<?> deleteAndTransferStatus(@PathVariable String boardId, @PathVariable Integer id, @PathVariable Integer newId,  @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED: No token provided.");
        }
        statusServices.deleteStatusAndTransfer(id, newId, boardId, token);
        return ResponseEntity.ok().body(new HashMap<>());
    }
    @GetMapping("/{boardId}/statuses/{id}")
    public ResponseEntity<?> getStatusDetail(@PathVariable String boardId, @PathVariable Integer id,  @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Forbidden: No token provided.");
        }
        try {
            Object statusDetail = statusServices.getStatusDetail(id, boardId, token);
            if (statusDetail.equals("403")){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(statusDetail);
            }else if(statusDetail.equals("error")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(statusDetail);
            }else if(statusDetail instanceof Status){
                return ResponseEntity.status(HttpStatus.OK).body(statusDetail);
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(statusDetail);
            }
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
    }
}
