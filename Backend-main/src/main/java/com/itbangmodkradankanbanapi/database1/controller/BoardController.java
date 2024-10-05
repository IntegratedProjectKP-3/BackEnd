package com.itbangmodkradankanbanapi.database1.controller;

import com.itbangmodkradankanbanapi.database1.DTO.BoardDTO_AddBoard;
import com.itbangmodkradankanbanapi.database1.DTO.HomePageTaskDTO;
import com.itbangmodkradankanbanapi.database1.DTO.TaskDTO3_V2;
import com.itbangmodkradankanbanapi.database1.DTO.TaskDTO3_V2_addTask;
import com.itbangmodkradankanbanapi.database1.entities.Board;
import com.itbangmodkradankanbanapi.database1.entities.Task;
import com.itbangmodkradankanbanapi.database1.repositories.TaskRepo;
import com.itbangmodkradankanbanapi.database1.service.ListMapper;
import com.itbangmodkradankanbanapi.database1.service.BoardAndTaskServices;
import com.itbangmodkradankanbanapi.database1.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
//@CrossOrigin(origins ="http://ip23kp3.sit.kmutt.ac.th:3000")
//@CrossOrigin(origins ="http://localhost:5173")
@CrossOrigin(origins = {"http://intproj23.sit.kmutt.ac.th","http://ip23kp3.sit.kmutt.ac.th:3000","http://localhost:5173"})
@RequestMapping("/boards")
public class BoardController {
    @Autowired
    private BoardAndTaskServices boardAndTaskServices;
    @Autowired
    private UserService userService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;
    @Autowired
    private TaskRepo taskRepo;

    @GetMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getBoardDetail (@PathVariable String boardId,@RequestHeader("Authorization") String token){
        try {
            Board newBoard = boardAndTaskServices.getBoardDetail(token, boardId);
            if (newBoard != null) {
                return ResponseEntity.status(HttpStatus.OK).body(newBoard);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("NOT FOUND");
        }
    }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Unable to create board: " + e.getMessage() + "\"}");
        }
    }
    //create new board
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> NewBoard(@Valid @RequestBody BoardDTO_AddBoard boardDTOAddBoard, @RequestHeader("Authorization") String token) {
        try {
            Board newBoard = boardAndTaskServices.createNewBoard(boardDTOAddBoard, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(newBoard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Unable to create board: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<?> getPrivateTask(@PathVariable String id,@RequestHeader("Authorization") String token)  {
        List<HomePageTaskDTO> task = boardAndTaskServices.getPrivateTask(id,token);
        if (task != null && !task.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(task);
        }
        else if(task == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("NOT FOUND");
        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body(task);
        }
    }
    @PostMapping("/{boardId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> NewPrivateTask(@Valid @RequestBody TaskDTO3_V2_addTask taskDTO3V2, @PathVariable String boardId, @RequestHeader("Authorization") String token) {
        try {
            TaskDTO3_V2 newTask = boardAndTaskServices.addPrivateTask(taskDTO3V2,boardId, token);
            if (newTask != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT FOUND");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
    @GetMapping("/{boardId}/tasks/{id}")
    public ResponseEntity<?> getTask(@PathVariable String boardId,@PathVariable Integer id, @RequestHeader("Authorization") String token){
        try {
            Task newTask = boardAndTaskServices.getFullTask(id,boardId,token);
            return ResponseEntity.status(HttpStatus.OK).body(newTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Unable to find: " + id + "\"}");
        }
    }
    @PutMapping("/{boardId}/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updatePrivateTask(@Valid @RequestBody TaskDTO3_V2_addTask taskDTO3V2, @PathVariable String boardId,@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        try {
            TaskDTO3_V2 newTask = boardAndTaskServices.updatePrivateTask(taskDTO3V2,id,boardId,token);
            return ResponseEntity.status(HttpStatus.OK).body(newTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Unable to create board: " + e.getMessage() + "\"}");
        }
    }
    @DeleteMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<TaskDTO3_V2> deletePrivateTask(@RequestHeader("Authorization") String token, @PathVariable String boardId, @PathVariable Integer taskId){
         TaskDTO3_V2 task = boardAndTaskServices.deletePrivateTask(taskId,boardId,token);
        return ResponseEntity.ok(task);
    }
// show own board
    @GetMapping
    public List<Board> getBoard(@RequestHeader("Authorization") String token){
        return boardAndTaskServices.showOwnBoard(token);
    }
// show all public task
    @GetMapping("/public/tasks")
    public List<HomePageTaskDTO> getPublicTask(){
        return boardAndTaskServices.getPublicTask();
    }
    @PostMapping("/public/tasks/add")
    public TaskDTO3_V2 addPublicTask(@Valid @RequestBody TaskDTO3_V2_addTask taskDTO3V2AddTask){
        return boardAndTaskServices.addPublicTask(taskDTO3V2AddTask);
    }
}

