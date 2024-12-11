package com.itbangmodkradankanbanapi.database1.controller;

import com.itbangmodkradankanbanapi.database1.DTO.StatusDTO;
import com.itbangmodkradankanbanapi.database1.DTO.TaskDTO3_V2;
import com.itbangmodkradankanbanapi.database1.DTO.TaskDTO3_V2_addTask;
import com.itbangmodkradankanbanapi.database1.entities.Board;
import com.itbangmodkradankanbanapi.database1.entities.Invite;
import com.itbangmodkradankanbanapi.database1.entities.Status;
import com.itbangmodkradankanbanapi.database1.repositories.BoardRepo;
import com.itbangmodkradankanbanapi.database1.repositories.InviteRepo;
import com.itbangmodkradankanbanapi.database1.repositories.StatusRepo;
import com.itbangmodkradankanbanapi.database1.service.BoardAndTaskServices;
import com.itbangmodkradankanbanapi.database1.service.InviteService;
import com.itbangmodkradankanbanapi.database1.service.StatusServices;
import com.itbangmodkradankanbanapi.database1.service.UserService;
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
    private BoardAndTaskServices boardAndTaskServices;
    @Autowired
    private InviteService inviteService;
    @Autowired
    private InviteRepo inviteRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private BoardRepo boardRepo;
    @GetMapping("/statuses")
    public List<StatusDTO> getAllStatus() {
        return statusServices.getAllStatus();
    }
    @GetMapping("/{boardId}/statuses")
    public ResponseEntity<?> getAllPrivateStatus(@PathVariable String boardId, @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            boardAndTaskServices.checkBoardPublicOrPrivate(boardId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("NOT_FOUND");
        }
        Board board = boardRepo.findById(boardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if (((token == null || token.isEmpty()) && board.getVisibility().equals("public"))){
            Object newTask = statusServices.findPrivateStatus(token, boardId);
            if (newTask instanceof List<?>) {
                return ResponseEntity.status(HttpStatus.OK).body(newTask);
            } else if(newTask.equals("403")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(newTask);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT FOUND");
            }
        }
        String oid = userService.getUserId(token);
        Invite myInvite = inviteRepo.findByBoardIdAndOid(boardId, oid);
        String username = userService.GetUserName(token);
        if ((token == null || token.isEmpty()) && !boardAndTaskServices.checkBoardPublicOrPrivate(boardId)
                && !board.getOwnerId().equals(username) && myInvite == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Forbidden: No token provided.");
        }
        else if(inviteService.listAllCollab(token,boardId).equals("403")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("403");
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
    public ResponseEntity<Object> addStatus(@Valid @RequestBody(required = false) StatusDTO status, @PathVariable String boardId,  @RequestHeader(value = "Authorization", required = false) String token) {
        if ((token == null || token.isEmpty()) && status == null) {
            System.out.println("status == null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED: No token provided.");
        }
        try {
            boardAndTaskServices.checkBoardPublicOrPrivate(boardId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("NOT_FOUND");
        }
        String oid = userService.getUserId(token);
        Invite myInvite = inviteRepo.findByBoardIdAndOid(boardId,oid);
        String username = userService.GetUserName(token);
        Board board = boardRepo.findById(boardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if (myInvite == null && !board.getOwnerId().equalsIgnoreCase(username)){
            System.out.println("if  myInvite null or not my board");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("FORBIDDEN");
        }
        else if (myInvite != null){
        if (myInvite.getAccess().equalsIgnoreCase("read") && !boardAndTaskServices.checkBoardPublicOrPrivate(boardId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("FORBIDDEN");
        }
        else if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED: No token provided.");
        }else if(status == null){
            System.out.println("status == null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("BAD_REQUEST");
        }
        else{
            StatusDTO createdStatus = statusServices.addStatus(status, token, boardId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
        }
        }else if (board.getOwnerId().equalsIgnoreCase(username)){
            StatusDTO createdStatus = statusServices.addStatus(status, token, boardId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStatus);
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("500");
        }
    }

    @PutMapping("/{boardId}/statuses/{id}")
    public ResponseEntity<Object> UpdatePrivateStatus(@PathVariable String boardId, @PathVariable Integer id,  @RequestHeader(value = "Authorization", required = false) String token, @Valid @RequestBody(required = false) StatusDTO statusDTO) {
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
        String oid = userService.getUserId(token);
        Invite myInvite = inviteRepo.findByBoardIdAndOid(boardId,oid);
        String username = userService.GetUserName(token);
        Board board = boardRepo.findById(boardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if (myInvite == null && !board.getOwnerId().equalsIgnoreCase(username)){
            System.out.println("if  myInvite null or not my board");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("FORBIDDEN");
        }
        else if (myInvite != null) {
            if (myInvite.getAccess().equalsIgnoreCase("read") && !boardAndTaskServices.checkBoardPublicOrPrivate(boardId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("FORBIDDEN");
            } else if (statusDTO == null || !isValid(statusDTO)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Bad Request: Invalid task data");
            }
            StatusDTO updateStatus = statusServices.updateStatus(id, statusDTO, token, boardId);
            if (updateStatus != null) {
                return ResponseEntity.status(HttpStatus.OK).body(updateStatus);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT FOUND");
            }
        }else if (board.getOwnerId().equalsIgnoreCase(username)){
            StatusDTO updateStatus = statusServices.updateStatus(id, statusDTO, token, boardId);
            if (updateStatus != null) {
                return ResponseEntity.status(HttpStatus.OK).body(updateStatus);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT FOUND");
            }
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("500");

        }
    }

    @DeleteMapping("/{boardId}/statuses/{id}")
    public ResponseEntity<?> deleteStatus(@PathVariable String boardId, @PathVariable Integer id,  @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED: No token provided.");
        }try {
            boardAndTaskServices.checkBoardPublicOrPrivate(boardId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("NOT_FOUND");
        }
        String oid = userService.getUserId(token);
        Invite myInvite = inviteRepo.findByBoardIdAndOid(boardId,oid);
        String username = userService.GetUserName(token);
        Board board = boardRepo.findById(boardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if (myInvite == null && !board.getOwnerId().equalsIgnoreCase(username)){
            System.out.println("if  myInvite null or not my board");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("FORBIDDEN");
        }
        assert myInvite != null;
        if (myInvite.getAccess().equalsIgnoreCase("read") && !boardAndTaskServices.checkBoardPublicOrPrivate(boardId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("FORBIDDEN");
        }        try{
            Object status = statusServices.deleteStatus(id, token, boardId);
            if(status.equals("deleted")){
                return ResponseEntity.ok().body(new HashMap<>());
            }else if (status.equals("You do not have permission to delete")){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(status);
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(status);
            }
        }catch (ResponseStatusException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
        }
    }

    @DeleteMapping("/{boardId}/statuses/{id}/{newId}")
    public ResponseEntity<?> deleteAndTransferStatus(@PathVariable String boardId, @PathVariable Integer id, @PathVariable Integer newId,  @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED: No token provided.");
        }
        try {
            boardAndTaskServices.checkBoardPublicOrPrivate(boardId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("NOT_FOUND");
        }        String oid = userService.getUserId(token);
        Invite myInvite = inviteRepo.findByBoardIdAndOid(boardId,oid);
        String username = userService.GetUserName(token);
        Board board = boardRepo.findById(boardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if (myInvite == null && !board.getOwnerId().equalsIgnoreCase(username)){
            System.out.println("if  myInvite null or not my board");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("FORBIDDEN");
        }
        if (myInvite != null || board.getOwnerId().equals(username)) {
            if (!board.getOwnerId().equals(username)&& !boardAndTaskServices.checkBoardPublicOrPrivate(boardId) && myInvite.getAccess().equalsIgnoreCase("read")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("FORBIDDEN");
        }
        statusServices.deleteStatusAndTransfer(id, newId, boardId, token);
        return ResponseEntity.ok().body(new HashMap<>());
    }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("500");
    }

    @GetMapping("/{boardId}/statuses/{id}")
    public ResponseEntity<?> getStatusDetail(@PathVariable String boardId, @PathVariable Integer id,  @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            boardAndTaskServices.checkBoardPublicOrPrivate(boardId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("NOT_FOUND");
        }
        String oid = userService.getUserId(token);
        Invite myInvite = inviteRepo.findByBoardIdAndOid(boardId, oid);
        String username = userService.GetUserName(token);
        Board board = boardRepo.findById(boardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if ((token == null || token.isEmpty()) && !boardAndTaskServices.checkBoardPublicOrPrivate(boardId)
                && !board.getOwnerId().equals(username) && myInvite == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Forbidden: No token provided.");
        }else if(inviteService.listAllCollab(token,boardId).equals("403")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("403");
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
            HttpStatus status = (HttpStatus) ex.getStatusCode();
            if (status == HttpStatus.FORBIDDEN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("FORBIDDEN: " + ex.getReason());
            } else if (status == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED: " + ex.getReason());
            } else if (status == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT FOUND: " + ex.getReason());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
            }
        }

    }
    private boolean isValid(StatusDTO statusDTO) {
        return statusDTO.getName() != null && !statusDTO.getName().trim().isEmpty();
    }
}
