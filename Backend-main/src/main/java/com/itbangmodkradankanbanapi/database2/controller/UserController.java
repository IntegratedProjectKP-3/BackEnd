package com.itbangmodkradankanbanapi.database2.controller;

import com.itbangmodkradankanbanapi.database2.service.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"http://ip23kp3.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th","http://ip23kp3.sit.kmutt.ac.th:3000","http://localhost:5173"})
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServices userService;
    @GetMapping("")
    public ResponseEntity<?> findAllUser(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.showAllUser());
    }
}
