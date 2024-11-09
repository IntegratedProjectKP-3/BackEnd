package com.itbangmodkradankanbanapi.database1.service;

import com.itbangmodkradankanbanapi.database1.DTO.AccessBoard;
import com.itbangmodkradankanbanapi.database1.DTO.InviteDTO;
import com.itbangmodkradankanbanapi.database1.DTO.PatchInvite;
import com.itbangmodkradankanbanapi.database1.entities.Board;
import com.itbangmodkradankanbanapi.database1.entities.Invite;
import com.itbangmodkradankanbanapi.database1.entities.UserInfo;
import com.itbangmodkradankanbanapi.database1.repositories.BoardRepo;
import com.itbangmodkradankanbanapi.database1.repositories.InviteRepo;
import com.itbangmodkradankanbanapi.database1.repositories.UserInfoRepo;
import com.itbangmodkradankanbanapi.database2.entities.User;
import com.itbangmodkradankanbanapi.database2.repositories.UserRepo;
import com.itbangmodkradankanbanapi.database2.service.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service

public class InviteService {
    @Autowired
    private InviteRepo inviteRepo;
    @Autowired
    private BoardAndTaskServices boardAndTaskServices;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BoardRepo boardRepo;
    @Autowired
    private UserService userService;

    public Object inviteToCoop(String token, String boardId, InviteDTO inviteDTO) {
        boardRepo.findById(boardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if (token == null || token.isEmpty()) {
            return "401";
        }
        if(!boardAndTaskServices.checkUsernameAndOwnerId(token, boardId)){
            return "403";
        }else if (inviteDTO.getAccessRight() == null ||(!inviteDTO.getAccessRight().equalsIgnoreCase("read")
                && !inviteDTO.getAccessRight().equalsIgnoreCase("write"))) {
            return "400";
        }
        if (boardAndTaskServices.checkUsernameAndOwnerId(token, boardId)) {
            User user = userRepo.findByEmail(inviteDTO.getEmail());
            if (user == null) {
                System.out.println((Object) null);
                return "404";
            }
            boardRepo.findById(boardId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
            String email = userService.getUserEmail(token);
//            String oid = userService.getUserId(token);
            Invite invite1 = inviteRepo.findByEmailAndBoardId(inviteDTO.getEmail(),boardId);
            System.out.println(invite1);
            if (invite1 != null || email.equals(inviteDTO.getEmail())) {
//                System.out.println(invite1);
//                System.out.println(email.equals(inviteDTO.getEmail()));
                return "409";
            }
            String newId;
            Optional<Invite> existingInvite;
            do {
                newId = UUID.randomUUID().toString().substring(0, 11).replace("-", "");
                existingInvite = inviteRepo.findById(newId);
            } while (existingInvite.isPresent());
            Invite invite = new Invite(newId, user.getName(), user.getEmail(), inviteDTO.getAccessRight(), boardId, null,user.getOid());
            System.out.println(inviteDTO.getAccessRight());
            inviteRepo.save(invite);
            return invite;
        }
        return "403";
    }
    public Object patchInvite(String token, String boardId, PatchInvite patchInvite,String oid){
        if (token == null){
            return "401";
        }
        Board board = boardRepo.findById(boardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        String name = userService.GetUserName(token);
        Invite invite = inviteRepo.findByBoardIdAndOid(boardId,oid);
        if (invite == null && !boardAndTaskServices.checkBoardPublicOrPrivate(boardId)){
            return "404";
        }else {
            assert invite != null;
            if((invite.getAccess().equalsIgnoreCase("read") || invite.getAccess().equalsIgnoreCase("write")) && !board.getOwnerId().equals(name)){
                System.out.println(board.getOwnerId());
                System.out.println(name);
                return "403";
            }
            else if (patchInvite.getAccessRight() == null || patchInvite.getAccessRight().isEmpty()
                    || (!patchInvite.getAccessRight().equalsIgnoreCase("read") && !patchInvite.getAccessRight().equalsIgnoreCase("write"))){
                return "400";
            }
        }
        invite.setAccess(patchInvite.getAccessRight());
        inviteRepo.save(invite);
        return invite;
    }
    public Object listAllCollab(String token, String boardId) {
        if(token == null){
            return "401";
        }
        String ownerId = userService.getUserId(token);
        String name = userService.GetUserName(token);
        Invite invite = inviteRepo.findByBoardIdAndOid(boardId,ownerId);
        Board board = boardRepo.findById(boardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if (invite == null && !boardAndTaskServices.checkBoardPublicOrPrivate(boardId)&& !board.getOwnerId().equals(name)){
            System.out.println(board.getOwnerId());
            System.out.println(name);
            return "403";
//            return new AccessBoard();
        }
        if (boardAndTaskServices.checkBoardPublicOrPrivate(boardId) || boardAndTaskServices.checkUsernameAndOwnerId(token, boardId)
                || (Objects.requireNonNull(invite).getAccess().equalsIgnoreCase("write"))
                || (Objects.requireNonNull(invite).getAccess().equalsIgnoreCase("read"))) {
            System.out.println("in if else");
            return inviteRepo.findAllByBoardId(boardId);
        }
        System.out.println("out if else");
        return "404";
    }

    public Object getDetail(String token, String boardId,String oid){
        if (token == null){
            return "404";
        }
        String ownerId = userService.getUserId(token);
        String name = userService.GetUserName(token);
        Invite invite = inviteRepo.findByBoardIdAndOid(boardId,oid);
        Invite myInvite = inviteRepo.findByBoardIdAndOid(boardId,ownerId);
        Board board = boardRepo.findById(boardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if (myInvite == null && !boardAndTaskServices.checkBoardPublicOrPrivate(boardId) &&  !board.getOwnerId().equals(name)){
             return "403";
        }
         if (invite == null && (board.getOwnerId().equals(name) || myInvite != null)){
             return "404";
         }
        System.out.println("-----------");
        System.out.println(invite);
        System.out.println(boardRepo.findByOwnerIdAndId(ownerId,boardId));
        System.out.println(!boardAndTaskServices.checkBoardPublicOrPrivate(boardId));
        System.out.println("-----------");
        if (boardAndTaskServices.checkBoardPublicOrPrivate(boardId) ||boardAndTaskServices.checkUsernameAndOwnerId(token, boardId)
                    || (Objects.requireNonNull(invite).getAccess().equalsIgnoreCase("write"))
                    || (Objects.requireNonNull(invite).getAccess().equalsIgnoreCase("read"))) {
                System.out.println("in if else");
                System.out.println(inviteRepo.findAllByOid(oid));
            return inviteRepo.findByBoardIdAndOid(boardId,oid);
        }
        System.out.println("out if else");
        return "404";
    }
    public Object deleteInvite(String token,String boardId,String oid){
        if (token == null || token.isEmpty()) {
            return "401";
        }
        String name = userService.GetUserName(token);
        String userId = userService.getUserId(token);
        Invite invite = inviteRepo.findByBoardIdAndOid(boardId,oid);
        Invite myInvite = inviteRepo.findByBoardIdAndOid(boardId,userId);
        Board board = boardRepo.findById(boardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if ((myInvite == null && !board.getOwnerId().equals(name))){
            System.out.println("Here");
            return "403";
        }
        else if (myInvite != null && (Objects.requireNonNull(myInvite).getAccess().equalsIgnoreCase("read")
                ||Objects.requireNonNull(myInvite).getAccess().equalsIgnoreCase("write"))
                && oid.equalsIgnoreCase(userId)){
            inviteRepo.deleteById(invite.getId());
            return "success";
        }
        if (invite == null){
            return "404";
        } else if (!board.getOwnerId().equals(name) && !invite.getName().equals(name)){
            System.out.println(invite.getName());
            System.out.println(name);
            System.out.println(invite.getName().equals(name));
            return "403";
       }else {
               inviteRepo.deleteById(invite.getId());
               return "success";
       }
    }
}
