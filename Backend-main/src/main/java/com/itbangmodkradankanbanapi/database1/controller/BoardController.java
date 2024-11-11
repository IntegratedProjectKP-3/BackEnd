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
import com.itbangmodkradankanbanapi.database2.entities.User;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private TaskRepo taskRepo;
    @Autowired
    private InviteService inviteService;
    @Autowired
    private InviteRepo inviteRepo;

    @GetMapping("/{boardId}")
    public ResponseEntity<?> getBoardDetail (@PathVariable String boardId,@RequestHeader(value = "Authorization", required = false) String token){
        try {
            boardAndTaskServices.checkBoardPublicOrPrivate(boardId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("NOT_FOUND");
        }
        if ((token == null || token.isEmpty()) && !boardAndTaskServices.checkBoardPublicOrPrivate(boardId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Forbidden: No token provided.");
        }
        else if(inviteService.listAllCollab(token,boardId).equals("403")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("403");
        }
        try {
            Object newBoard = boardAndTaskServices.getBoardDetail(token, boardId);
            if (newBoard == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("NOT FOUND");
            }else if(newBoard.equals("bad request")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(newBoard);
            }else if(newBoard.equals("403")){
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(newBoard);
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
        try {
            boardAndTaskServices.checkBoardPublicOrPrivate(boardId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("NOT_FOUND");
        }
        Object task = boardAndTaskServices.getPrivateTask(boardId,token);
         if(task == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("NOT FOUND");
        }
        else if (task.equals("Access denied, you do not have permission to view this page")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(task);
        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body(task);
        }
    }
    @PostMapping("/{boardId}/tasks")
    public ResponseEntity<?> NewPrivateTask(@Valid @RequestBody(required = false) TaskDTO3_V2_addTask taskDTO3V2,
                                            @PathVariable String boardId,
                                            @RequestHeader(value = "Authorization", required = false) String token) {
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
            System.out.println("if  myInvite null or not my board");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("FORBIDDEN");
        }
        if (myInvite != null || board.getOwnerId().equals(username)) {
            if (!board.getOwnerId().equals(username)&& !boardAndTaskServices.checkBoardPublicOrPrivate(boardId) && myInvite.getAccess().equalsIgnoreCase("read")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("FORBIDDEN");
            } else if (taskDTO3V2 == null || !isValid(taskDTO3V2)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Bad Request: Invalid task data");
            }
            try {
                TaskDTO3_V2 newTask = boardAndTaskServices.addPrivateTask(taskDTO3V2, boardId, token);
                if (newTask != null) {
                    return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(newTask);
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(e.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("500");
    }
    @GetMapping("/{boardId}/tasks/{id}")
    public ResponseEntity<?> getTask(@PathVariable String boardId,@PathVariable Integer id, @RequestHeader(value = "Authorization", required = false) String token){
        try {
            boardAndTaskServices.checkBoardPublicOrPrivate(boardId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("NOT_FOUND");
        }
        if ((token == null || token.isEmpty())&& !boardAndTaskServices.checkBoardPublicOrPrivate(boardId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Forbidden: No token provided.");
        }
        else if(inviteService.listAllCollab(token,boardId).equals("403")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("403");
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
            System.out.println("if  myInvite null or not my board");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("FORBIDDEN");
        }
        if (myInvite != null || board.getOwnerId().equals(username)) {
            if (!board.getOwnerId().equals(username) && !boardAndTaskServices.checkBoardPublicOrPrivate(boardId) && myInvite.getAccess().equalsIgnoreCase("read")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("FORBIDDEN");
            } else if (taskDTO3V2 == null || !isValid(taskDTO3V2)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Bad Request: Invalid task data");
            }
            try {
                TaskDTO3_V2 newTask = boardAndTaskServices.updatePrivateTask(taskDTO3V2, id, boardId, token);
                return ResponseEntity.status(HttpStatus.OK).body(newTask);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Unable to create board: " + e.getMessage() + "\"}");
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("500");
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
        if (myInvite != null || board.getOwnerId().equals(username)) {
            if (!board.getOwnerId().equals(username) && !boardAndTaskServices.checkBoardPublicOrPrivate(boardId) && myInvite.getAccess().equalsIgnoreCase("read")) {
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
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("500");
    }


// show own board
    @GetMapping
    public AccessBoard getBoard(@RequestHeader(value = "Authorization", required = false) String token){
        return boardAndTaskServices.accessBoard(token);
    }
// show all public task
    @GetMapping("/public/tasks")
    public List<HomePageTaskDTO> getPublicTask(){
        return boardAndTaskServices.getPublicTask();
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<?> changeMode(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable String boardId,@RequestBody(required = false) VisibilityDTO visibility){
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED: No token provided.");
        }
        try {
            boardAndTaskServices.checkBoardPublicOrPrivate(boardId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("NOT_FOUND");
        }
        if(!boardAndTaskServices.checkUsernameAndOwnerId(token,boardId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("FORBIDDEN");
        }else if (visibility == null || visibility.getVisibility() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Bad Request: Invalid task data");
        }
        Object board =boardAndTaskServices.TogglePrivateAndPublicBoard(boardId,token,visibility);
        if(board.equals("You do not have permission to change board visibility mode")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(board);
        }else if(board.equals("bad request") || board.equals("There is a problem. Please try again later")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(board);
        }
        else{
            return ResponseEntity.status(HttpStatus.OK).body(board);
        }
    }
    private boolean isValid(TaskDTO3_V2_addTask task) {
        return task.getTitle() != null && !task.getTitle().trim().isEmpty();
    }
}

