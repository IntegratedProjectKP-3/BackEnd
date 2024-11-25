package com.itbangmodkradankanbanapi.database1.controller;

import com.itbangmodkradankanbanapi.database1.DTO.*;
import com.itbangmodkradankanbanapi.database1.entities.Board;
import com.itbangmodkradankanbanapi.database1.entities.Invite;
import com.itbangmodkradankanbanapi.database1.entities.Task;
import com.itbangmodkradankanbanapi.database1.repositories.BoardRepo;
import com.itbangmodkradankanbanapi.database1.repositories.InviteRepo;
import com.itbangmodkradankanbanapi.database1.repositories.TaskRepo;
import com.itbangmodkradankanbanapi.database1.service.InviteService;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
//@CrossOrigin(origins ="http://ip23kp3.sit.kmutt.ac.th:3000")
//@CrossOrigin(origins ="http://localhost:5173")
@CrossOrigin(origins = {"http://ip23kp3.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th","http://ip23kp3.sit.kmutt.ac.th:3000","http://localhost:5173"})
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
    private BoardRepo boardRepo;
    @Autowired
    private InviteService inviteService;
    @Autowired
    private InviteRepo inviteRepo;
    @Autowired
    private ToolForController toolForController;

    @GetMapping("/{boardId}")
    public ResponseEntity<?> getBoardDetail (@PathVariable String boardId,@RequestHeader(value = "Authorization", required = false) String token){
        ResponseEntity<?> check =  toolForController.checkPublicAndPrivate(token,boardId);
        if (check != null){
            System.out.println("in controller : " + check);
            return check;
        }
        try {
            Object newBoard = boardAndTaskServices.getBoardDetail(token, boardId);
            if (newBoard instanceof String){
                return toolForController.errorResponse(newBoard);
            }else{
                return ResponseEntity.status(HttpStatus.OK).body(newBoard);
        }
    }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{error : Unable to get board: " + e.getMessage() + "}");
        }
    }
    //create new board
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> NewBoard(@Valid @RequestBody BoardDTO_AddBoard boardDTOAddBoard, @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED: No token provided.");
        }
        try {
            Board newBoard = boardAndTaskServices.createNewBoard(boardDTOAddBoard, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(newBoard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Unable to create board: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/{boardId}/tasks")
    public ResponseEntity<?> getPrivateTask(@PathVariable String boardId,@RequestHeader(value = "Authorization", required = false) String token)  {
        ResponseEntity<?> check =  toolForController.checkPublicAndPrivate(token,boardId);
        if (check != null){
            return check;
        }
        Object task = boardAndTaskServices.getPrivateTask(boardId,token);
        if (task instanceof String){
            return toolForController.errorResponse(task);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(task);
        }
    }
    @PostMapping("/{boardId}/tasks")
    public ResponseEntity<?> NewPrivateTask(@Valid @RequestBody(required = false) TaskDTO3_V2_addTask taskDTO3V2,
                                            @PathVariable String boardId,
                                            @RequestHeader(value = "Authorization", required = false) String token) {
        ResponseEntity<?> response = toolForController.checkTokenAndCheckPublicPrivate(token,boardId);
        if(response != null){
            return response;
        }
        ResponseEntity<?> response2 = toolForController.checkInvite(token,boardId,taskDTO3V2);
        if (response2 != null) {
            return response2;
        }
            try {
                TaskDTO3_V2 newTask = boardAndTaskServices.addPrivateTask(taskDTO3V2, boardId, token);
                if (newTask != null) {
                    return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("404");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage());
            }
    }
    @GetMapping("/{boardId}/tasks/{id}")
    public ResponseEntity<?> getTask(@PathVariable String boardId,@PathVariable Integer id, @RequestHeader(value = "Authorization", required = false) String token){
        ResponseEntity<?> check =  toolForController.checkPublicAndPrivate(token,boardId);
        if (check != null){
            return check;
        }
        try {
            System.out.println("boardAndTaskServices");
            Object newTask = boardAndTaskServices.getFullTask(id,boardId,token);
            if (newTask instanceof Task){
                return ResponseEntity.status(HttpStatus.OK).body(newTask);
            }else if (newTask.equals("403")){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(newTask);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(newTask);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Unable to find: " + id + "\"}");
        }
    }
    @PutMapping("/{boardId}/tasks/{id}")
    public ResponseEntity<?> updatePrivateTask(@Valid @RequestBody(required = false) TaskDTO3_V2_addTask taskDTO3V2, @PathVariable String boardId,@PathVariable Integer id,@RequestHeader(value = "Authorization", required = false) String token) {
        ResponseEntity<?> response = toolForController.checkTokenAndCheckPublicPrivate(token,boardId);
        if(response != null){
            return response;
        }
        ResponseEntity<?> response2 = toolForController.checkInvite(token,boardId,taskDTO3V2);
        if (response2 != null) {
            return response2;
        }
            try {
                TaskDTO3_V2 newTask = boardAndTaskServices.updatePrivateTask(taskDTO3V2, id, boardId, token);
                return ResponseEntity.status(HttpStatus.OK).body(newTask);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("401");
            }
        }
    @DeleteMapping("/{boardId}/tasks/{taskId}")
    public ResponseEntity<?> deletePrivateTask(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable String boardId, @PathVariable Integer taskId) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED: No token provided.");
        }
        try {
            boardAndTaskServices.checkBoardPublicOrPrivate(boardId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("NOT_FOUND");
        }
        String oid = userService.getUserId(token);
        Invite myInvite = inviteRepo.findByBoardIdAndOid(boardId, oid);
        String username = userService.GetUserName(token);
        Board board = boardRepo.findById(boardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if (myInvite == null && !board.getOwnerId().equalsIgnoreCase(username)) {
            System.out.println("if myInvite null or not my board");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("FORBIDDEN");
        }
        System.out.println(board.getOwnerId());
        if (myInvite != null || board.getOwnerId().equals(username)) {
            if (!board.getOwnerId().equals(username) && !boardAndTaskServices.checkBoardPublicOrPrivate(boardId) && myInvite.getAccess().equalsIgnoreCase("read")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("FORBIDDEN");
            }
                try {
                    Object task = boardAndTaskServices.deletePrivateTask(taskId, boardId, token);
                    if (task instanceof TaskDTO3_V2) {
                        return ResponseEntity.ok(task);
                    } else if (task.equals("403")) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(task);
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Not found");
                    }
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("{\"error\": \"Unable to create board: " + e.getMessage() + "\"}");
                }
            }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("500");
    }

// show own board
    @GetMapping
    public AccessBoard getBoard(@RequestHeader(value = "Authorization", required = false) String token){
        return boardAndTaskServices.accessBoard(token);
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<?> changeMode(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable String boardId,@RequestBody(required = false) VisibilityDTO visibility){
        ResponseEntity<?> check = toolForController.checkPublicAndPrivate(token,boardId);
        if(check != null){
            return check;
        }else if (visibility == null || visibility.getVisibility() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("400");
        }
        Object board =boardAndTaskServices.TogglePrivateAndPublicBoard(boardId,token,visibility);
        if(board instanceof String){
            return toolForController.errorResponse(board);
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(board);
        }
    }
    private boolean isValid(TaskDTO3_V2_addTask task) {
        return task.getTitle() != null && !task.getTitle().trim().isEmpty();
    }
}

