package com.itbangmodkradankanbanapi.Controller;

import com.itbangmodkradankanbanapi.entities.Status;
import com.itbangmodkradankanbanapi.repositories.StatusRepo;
import com.itbangmodkradankanbanapi.service.StatusServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins ="http://ip23kp3.sit.kmutt.ac.th:3000")
//@CrossOrigin(origins ="http://localhost:5173")
@RequestMapping("/statuses")
public class StatusController {
    @Autowired
    private StatusServices statusServices;
    @Autowired
    private StatusRepo statusRepo;

    @GetMapping()
    public  List<Status> getAllStatus(){
        return statusServices.getAllStatus();
    }
    @PostMapping()
    public List<Status> addStatus(@RequestBody List<Status> status) {
        for(Status status1 : status){
            statusServices.AddStatus(status1);
        }
        return  status;
    }
    @DeleteMapping("/{id}")
    public Status Delete(@PathVariable Integer id) {
        Status status = statusServices.findId(id);
        statusServices.deleteStatus(id);
        return status;
    }
}
