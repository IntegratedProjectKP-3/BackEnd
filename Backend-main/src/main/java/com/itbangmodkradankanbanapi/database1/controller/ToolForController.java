package com.itbangmodkradankanbanapi.database1.controller;

import com.itbangmodkradankanbanapi.database1.DTO.TaskDTO3_V2_addTask;
import com.itbangmodkradankanbanapi.database1.entities.Board;
import com.itbangmodkradankanbanapi.database1.entities.Invite;
import com.itbangmodkradankanbanapi.database1.repositories.BoardRepo;
import com.itbangmodkradankanbanapi.database1.repositories.InviteRepo;
import com.itbangmodkradankanbanapi.database1.service.BoardAndTaskServices;
import com.itbangmodkradankanbanapi.database1.service.InviteService;
import com.itbangmodkradankanbanapi.database1.service.ListMapper;
import com.itbangmodkradankanbanapi.database1.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ToolForController {
    @Autowired
    private BoardAndTaskServices boardAndTaskServices;
    @Autowired
    private InviteService inviteService;
    @Autowired
    private UserService userService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;
    @Autowired
    private BoardRepo boardRepo;
    @Autowired
    private InviteRepo inviteRepo;

    public ResponseEntity<?> checkTokenAndCheckPublicPrivate(String token, String boardId){
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED: No token provided.");
        }
        try {
            boardAndTaskServices.checkBoardPublicOrPrivate(boardId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("404");
        }
        return null;
    }
    public ResponseEntity<?> checkPublicAndPrivate (String token , String boardId){
        ResponseEntity<?> response = checkTokenAndCheckPublicPrivate(token,boardId);
        if (response != null){
            return response;
        }
        if ((!boardAndTaskServices.checkBoardPublicOrPrivate(boardId)) && inviteService.listAllCollab(token,boardId).equals("403") ) {
            System.out.println(!boardAndTaskServices.checkBoardPublicOrPrivate(boardId));
            System.out.println(inviteService.listAllCollab(token,boardId).equals("403"));
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("403");
        }
        return null;
    }
    public ResponseEntity<?> checkInvite(String token, String boardId, TaskDTO3_V2_addTask taskDTO3V2) {
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
            return null;
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("500");
    }

    private boolean isValid(TaskDTO3_V2_addTask task) {
        return task.getTitle() != null && !task.getTitle().trim().isEmpty();
    }

    public ResponseEntity<?> errorResponse (Object body){
        return switch (body.toString()) {
            case "400" -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
            case "401" -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
            case "403" -> ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
            case "404" -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            case "409" -> ResponseEntity.status(HttpStatus.CONFLICT).body(body);
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        };
    }

}
