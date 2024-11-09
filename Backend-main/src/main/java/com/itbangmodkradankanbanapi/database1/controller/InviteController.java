package com.itbangmodkradankanbanapi.database1.controller;

import com.itbangmodkradankanbanapi.database1.DTO.InviteDTO;
import com.itbangmodkradankanbanapi.database1.DTO.PatchInvite;
import com.itbangmodkradankanbanapi.database1.DTO.StatusDTO;
import com.itbangmodkradankanbanapi.database1.entities.Invite;
import com.itbangmodkradankanbanapi.database1.entities.Status;
import com.itbangmodkradankanbanapi.database1.service.BoardAndTaskServices;
import com.itbangmodkradankanbanapi.database1.service.InviteService;
import com.itbangmodkradankanbanapi.database1.service.StatusServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://ip23kp3.sit.kmutt.ac.th","http://intproj23.sit.kmutt.ac.th","http://ip23kp3.sit.kmutt.ac.th:3000","http://localhost:5173"})
@RequestMapping("/boards")
public class InviteController {
    @Autowired
    private InviteService inviteService;
    @GetMapping("/{boardId}/collabs")
    public ResponseEntity<?> getListInvite(@PathVariable String boardId, @RequestHeader(value = "Authorization", required = false) String token) {
        Object invites = inviteService.listAllCollab(token,boardId);
        if (invites instanceof List<?>){
            return ResponseEntity.status(HttpStatus.OK).body(invites);
        }else{
            return errorResponse(invites);
        }
    }
    @GetMapping("{boardId}/collabs/{oid}")
    public ResponseEntity<?> getInviteDetail(@PathVariable String boardId,@PathVariable String oid, @RequestHeader(value = "Authorization", required = false) String token) {
        Object invites = inviteService.getDetail(token, boardId, oid);
        if(invites == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not found");
        }else if (invites instanceof Invite){
            return ResponseEntity.status(HttpStatus.OK).body(invites);
        }else{
            return errorResponse(invites);
        }
    }
        @PostMapping("{boardId}/collabs")
    public ResponseEntity<?> newInvite(@PathVariable String boardId, @RequestHeader(value = "Authorization", required = false) String token ,@Valid@RequestBody(required = false)InviteDTO inviteDTO ) {
        Object invites = inviteService.inviteToCoop(token,boardId,inviteDTO);
        if(invites instanceof Invite){
            return ResponseEntity.status(HttpStatus.CREATED).body(invites);
        }else{
            return errorResponse(invites);
        }
    }
    @PatchMapping("{boardId}/collabs/{oid}")
    public ResponseEntity<?> patchCollabs(@PathVariable String boardId,@PathVariable String oid, @RequestHeader(value = "Authorization", required = false) String token , @Valid@RequestBody(required = false) PatchInvite patchInvite ) {
        Object invite = inviteService.patchInvite(token,boardId,patchInvite,oid);
        if(invite instanceof Invite){
            return ResponseEntity.status(HttpStatus.OK).body(invite);
        }else{
            return errorResponse(invite);
        }
    }
    @DeleteMapping("{boardId}/collabs/{oid}")
    public ResponseEntity<?> deleteInvite(@PathVariable String boardId,@PathVariable String oid, @RequestHeader(value = "Authorization", required = false) String token ) {
        Object invite = inviteService.deleteInvite(token,boardId,oid);
        if(invite.equals("success")){
            return ResponseEntity.status(HttpStatus.OK).body(invite);
        }else{
            return errorResponse(invite);
        }
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
