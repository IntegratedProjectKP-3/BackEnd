package com.itbangmodkradankanbanapi.database1.service;

import com.itbangmodkradankanbanapi.database1.DTO.StatusDTO;
import com.itbangmodkradankanbanapi.database1.DTO.TaskDTO3_V2;
import com.itbangmodkradankanbanapi.database1.DTO.TaskDTO3_V2_addTask;
import com.itbangmodkradankanbanapi.database1.entities.Board;
import com.itbangmodkradankanbanapi.database1.entities.Invite;
import com.itbangmodkradankanbanapi.database1.entities.Status;
import com.itbangmodkradankanbanapi.database1.entities.Task;
import com.itbangmodkradankanbanapi.database1.repositories.BoardRepo;
import com.itbangmodkradankanbanapi.database1.repositories.InviteRepo;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import com.itbangmodkradankanbanapi.database1.repositories.StatusRepo;
import com.itbangmodkradankanbanapi.database1.repositories.TaskRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service

public class StatusServices {
    @Autowired
    private BoardAndTaskServices boardAndTaskServices;
    @Autowired
    private StatusRepo statusRepo;
    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ListMapper listMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private BoardRepo boardRepo;
    @Autowired
    private InviteService inviteService;
    @Autowired
    private InviteRepo inviteRepo;
    public static <Z, X> List<X> mapList(List<Z> sourceList, Class<X> destinationType) {
        ModelMapper modelMapper = new ModelMapper();
        return sourceList.stream()
                .map(element -> modelMapper.map(element, destinationType))
                .collect(Collectors.toList());
    }

    public Status findId(Integer Id) {
        return statusRepo.findById(Id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "status " + Id + " does not exist !!!"));
    }

    public Object findPrivateStatus(String token, String boardId) {
        Board board1 = boardRepo.findById(boardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        String name = userService.GetUserName(token);
        Invite invite = inviteRepo.findByName(name);
        if (board1.getVisibility().equals("public") || invite != null){
            List<Status> status = statusRepo.findAllByBoardId(boardId);
            return mapList(status, StatusDTO.class);
        }
        List<Board> OwnBoard = boardAndTaskServices.showOwnBoard(token);
        if (OwnBoard.stream().anyMatch(board -> board.getId().equals(boardId))) {
            List<Status> status = statusRepo.findAllByBoardId(boardId);
            return mapList(status, StatusDTO.class);
        } else if (board1.getVisibility().equals("private")) {
            return "403";
        }
        return "error";
    }

    public List<StatusDTO> getAllStatus() {
        List<Status> status = statusRepo.findAll();
        return mapList(status, StatusDTO.class);
    }

    @Transactional
    public StatusDTO addStatus(StatusDTO newStatus, String token, String boardId) {
        String name = userService.GetUserName(token);
        Invite invite = inviteRepo.findByName(name);
        boardRepo.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if (boardAndTaskServices.showOwnBoard(token).stream().anyMatch(board -> board.getId().equals(boardId))
                || (invite != null && invite.getAccess().equalsIgnoreCase("write"))) {
                List<Status> statusList = statusRepo.findAllByNameIgnoreCase(newStatus.getName());
                if (!statusList.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not duplicate name");
                }
                Status status = modelMapper.map(newStatus, Status.class);
                status.setName(newStatus.getName().trim());
                status.setBoardId(boardId);
                Status updatedStatus = statusRepo.saveAndFlush(status);
                return modelMapper.map(updatedStatus, StatusDTO.class);
        }
        return null;
    }

    @Transactional
    public StatusDTO updateStatus(Integer id, StatusDTO status, String token, String boardId) {
        String name = userService.GetUserName(token);
        Invite invite = inviteRepo.findByName(name);
        boardRepo.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if (boardAndTaskServices.showOwnBoard(token).stream().anyMatch(board -> board.getId().equals(boardId))
                || (invite != null && invite.getAccess().equalsIgnoreCase("write"))) {
                Status currentStatus = statusRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "status does not exist !!!"));
                currentStatus.setName(status.getName().trim());
                if (status.getDescription() != null) {
                    currentStatus.setDescription(status.getDescription());
                }
                statusRepo.saveAndFlush(currentStatus);
                return modelMapper.map(currentStatus, StatusDTO.class);
            }
        return null;
    }

    @Transactional
    public Object deleteStatus(Integer id, String token, String boardId) {
        List<Board> OwnBoard = boardAndTaskServices.showOwnBoard(token);
        Board board1 = boardRepo.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        statusRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "statusId does not exist !!!"));
        String name = userService.GetUserName(token);
        Invite invite = inviteRepo.findByName(name);
        if (OwnBoard.stream().anyMatch(board -> board.getId().equals(boardId))
                || (invite != null && invite.getAccess().equalsIgnoreCase("write"))) {
            Status status = statusRepo.findById(id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
            statusRepo.delete(status);
            return "deleted";
        }else if(board1.getVisibility().equals("private")){
        return "You do not have permission to delete";
        } else{
            return "error";
        }
    }
    @Transactional
    public void deleteStatusAndTransfer(Integer id, Integer newId, String boardId, String token) {
        List<Board> OwnBoard = boardAndTaskServices.showOwnBoard(token);
        String name = userService.GetUserName(token);
        Invite invite = inviteRepo.findByName(name);
        if (OwnBoard.stream().anyMatch(board -> board.getId().equals(boardId))
                || (invite != null && invite.getAccess().equalsIgnoreCase("write"))) {
                Status status = statusRepo.findById(id)
                        .orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
                Status transferStatus = statusRepo.findById(newId)
                        .orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));

                List<Task> taskV2s = status.getTaskList();
                taskV2s.forEach(task -> task.setStatus(transferStatus));
                taskRepo.saveAll(taskV2s);
                statusRepo.delete(status);
        }
    }
    @Transactional
    public Object getStatusDetail(Integer id, String boardId, String token) {
        Board board1 = boardRepo.findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if(board1.getVisibility().equals("public")){
            return statusRepo.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "statusId does not exist !!!"));
        }
        List<Board> ownBoard = boardAndTaskServices.showOwnBoard(token);
        String name = userService.GetUserName(token);
        Invite invite = inviteRepo.findByName(name);
        if (invite != null ||board1.getVisibility().equals("public") ||
                ownBoard.stream().anyMatch(board -> board.getId().equals(boardId))) {
            return statusRepo.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "statusId does not exist !!!"));
        }
        if (board1.getVisibility().equals("private") && ownBoard.stream().noneMatch(board -> board.getId().equals(boardId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have access to this board.");
        }
        return null;
    }

}
