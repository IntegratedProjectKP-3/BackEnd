package com.itbangmodkradankanbanapi.database1.service;

import com.itbangmodkradankanbanapi.database1.DTO.*;
import com.itbangmodkradankanbanapi.database1.entities.Board;
import com.itbangmodkradankanbanapi.database1.entities.Invite;
import com.itbangmodkradankanbanapi.database1.entities.Status;
import com.itbangmodkradankanbanapi.database1.entities.Task;
import com.itbangmodkradankanbanapi.database1.repositories.BoardRepo;
import com.itbangmodkradankanbanapi.database1.repositories.InviteRepo;
import com.itbangmodkradankanbanapi.database1.repositories.StatusRepo;
import com.itbangmodkradankanbanapi.database1.repositories.TaskRepo;
import com.itbangmodkradankanbanapi.database2.entities.User;
import com.itbangmodkradankanbanapi.database2.repositories.UserRepo;
import com.itbangmodkradankanbanapi.exception.ItemNotFoundForUpdateAndDelete;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class    BoardAndTaskServices {
    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    private StatusRepo statusRepo;
    @Autowired
    private BoardRepo boardRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private InviteRepo inviteRepo;
    @Autowired
    private UserRepo userRepo;
    public Object getPrivateTask(String BoardId,String token){
        Board board1 = boardRepo.findById(BoardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if (token == null || token.isEmpty()) {
            token = "default-token";
            if(board1.getVisibility().equals("private")){
                return "Access denied, you do not have permission to view this page";
            }else if(board1.getVisibility().equals("public")){
                if (taskRepo.findAllByBoardId(BoardId) == null || taskRepo.findAllByBoardId(BoardId).isEmpty()){
                    return new ArrayList<>();
                }
                return listMapper.mapList(taskRepo.findAllByBoardId(BoardId),HomePageTaskDTO.class,modelMapper);
            }
            return null;
        }
        String name = userService.GetUserName(token);
        if (showOwnBoard(token).stream().anyMatch(board -> board.getId().equals(BoardId))|| inviteRepo.findByName(name) != null || board1.getVisibility().equals("public")) {
            if (taskRepo.findAllByBoardId(BoardId) == null || taskRepo.findAllByBoardId(BoardId).isEmpty()){
                return new ArrayList<>();
            }
            return listMapper.mapList(taskRepo.findAllByBoardId(BoardId),HomePageTaskDTO.class,modelMapper);
        }
        else if (board1.getVisibility().equals("private")){
            System.out.println("private");
            return "Access denied, you do not have permission to view this page";
        }
        return null;
    }
    public TaskDTO3_V2  addPrivateTask(TaskDTO3_V2_addTask newTask,String boardId,String token){
        if (newTask == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access: Token is required.");
        }
        String name = userService.GetUserName(token);
        Invite invite = inviteRepo.findByName(name);
        if (showOwnBoard(token).stream().anyMatch(board -> board.getId().equals(boardId))
                || (invite != null && invite.getAccess().equalsIgnoreCase("write"))) {
                Status statusObject = statusRepo.findById(newTask.getStatus()).orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
                if (Objects.equals(statusObject.getBoardId(),boardId)&&
                        !Objects.equals(statusObject.getBoardId(), "public")){
                    Task task = modelMapper.map(newTask, Task.class);
                    task.setStatus(statusObject);
                    if(!Objects.equals(task.getBoardId(), "public")){
                        task.setBoardId(boardId);
                    }
                    taskRepo.save(task);
                    return modelMapper.map(task, TaskDTO3_V2.class);
                }
            }
        return null;
    }
    public TaskDTO3_V2  updatePrivateTask(TaskDTO3_V2_addTask newTask,Integer TaskId,String boardId,String token) {
        boardRepo.findById(boardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        String name = userService.GetUserName(token);
        Invite invite = inviteRepo.findByName(name);
        if (showOwnBoard(token).stream().anyMatch(board -> board.getId().equals(boardId))
                || (invite != null && invite.getAccess().equalsIgnoreCase("write"))) {
            Task existingTask = taskRepo.findById(TaskId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskId does not exist !!!"));
        existingTask.setDescription(newTask.getDescription());
        existingTask.setTitle(newTask.getTitle());
        existingTask.setAssignees(newTask.getAssignees());
        Status statusObject = statusRepo.findById(newTask.getStatus()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "StatusId does not exist !!!"));
        existingTask.setStatus(statusObject);
        existingTask.setBoardId(boardId);
        Task task = taskRepo.save(existingTask);
        return modelMapper.map(task, TaskDTO3_V2.class);
    }
        return null;
        }
public Object  deletePrivateTask(Integer TaskId,String boardId,String token) {
    Board board1 = boardRepo.findById(boardId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
    String name = userService.GetUserName(token);
    Invite invite = inviteRepo.findByName(name);
    if (showOwnBoard(token).stream().anyMatch(board -> board.getId().equals(boardId))
            || (invite != null && invite.getAccess().equalsIgnoreCase("write"))) {
            Task task =  taskRepo.findById(TaskId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskId does not exist !!!"));
            TaskDTO3_V2 taskDTO = modelMapper.map(task, TaskDTO3_V2.class);
            taskRepo.delete(task);
            return taskDTO;
    }
    else if (board1.getVisibility().equals("private")){
        System.out.println("private");
        return "403";
    }
    return null;
}
    public List<HomePageTaskDTO> getPublicTask(){
        return listMapper.mapList(taskRepo.findAllByBoardId("public"),HomePageTaskDTO.class,modelMapper);
    }
    @Transactional
    public Board createNewBoard(BoardDTO_AddBoard name, String token) {
        String newId;
        Optional<Board> existingBoard;
        do {
            newId = UUID.randomUUID().toString().substring(0, 11).replace("-", "");
            existingBoard = boardRepo.findById(newId);
        } while (existingBoard.isPresent());
        Board newBoard = new Board(newId, name.getName(), userService.GetUserName(token),
                null, null,"private");
        for(int i=1; i < 5;i++){
            Status status = statusRepo.findById(i).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "status does not exist !!!"));
            Status newStatus = new Status(null,status.getName() +" for " +
                    newId,status.getDescription(),newId,null);
            statusRepo.save(newStatus);
        }
        boardRepo.save(newBoard);
        return newBoard;
    }
    public AccessBoard accessBoard(String token) {
        String name = userService.GetUserName(token);
        String id = userService.getUserId(token);
        List<Board> board =  boardRepo.findAllByOwnerId(name);
        System.out.println(board);
        List<Invite> invite = inviteRepo.findAllByOid(id);
        for (Invite invite1 : invite){
            Board board1 = boardRepo.findById(invite1.getBoardId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
            board.add(board1);
        }
        AccessBoard accessBoard = new AccessBoard();
        accessBoard.setBoards(board);
        accessBoard.setCollab(invite);
        if (accessBoard.getBoards() != null && !accessBoard.getBoards().isEmpty()){
            return accessBoard;
        }
        return accessBoard;
    }

    public List<Board> showOwnBoard(String token) {
        String name = userService.GetUserName(token);
        List<Board> board =  boardRepo.findAllByOwnerId(name);
        System.out.println(board);
        if (board != null){
            return board;
        }
        return new ArrayList<>();
    }

    public Object getBoardDetail(String token,String boardId){
        Board board1 = boardRepo.findById(boardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        String name = userService.GetUserName(token);
        Invite invite = inviteRepo.findByName(name);
        if(board1.getVisibility().equals("public") || invite != null){
            return boardRepo.findById(boardId).orElseThrow(()->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,"TaskId does not exist !!!"));
        }
        List<Board> OwnBoard = showOwnBoard(token);
        if (OwnBoard.stream().anyMatch(board -> board.getId().equals(boardId))) {
            return boardRepo.findById(boardId).orElseThrow(()->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,"TaskId does not exist !!!"));
        }else if(board1.getVisibility().equals("private")){
            return "403";
        }
        return "bad request";
    }
    public Object getFullTask(Integer id,String boardId,String token){
        Board board1 = boardRepo.findById(boardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if(token == null && board1.getVisibility().equals("public")){
            return taskRepo.findById(id).orElseThrow(()->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,"TaskId does not exist !!!"));
        }else{
        List<Board> OwnBoard = showOwnBoard(token);
        System.out.println(boardId);
            String name = userService.GetUserName(token);
            Invite invite = inviteRepo.findByName(name);
            if (OwnBoard.stream().anyMatch(board -> board.getId().equals(boardId))
                    || invite != null || board1.getVisibility().equals("public")) {
            System.out.println("id : " + id);
                return taskRepo.findById(id).orElseThrow(()->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,"TaskId does not exist !!!"));
        }else if(board1.getVisibility().equals("private")){
            return "403";
        }
        System.out.println("null");
        return null;
        }
    }
    @Transactional
    public Object TogglePrivateAndPublicBoard(String boardId, String token, VisibilityDTO visibility){
        String name = userService.GetUserName(token);
        Invite invite = inviteRepo.findByName(name);
        if (showOwnBoard(token).stream().anyMatch(board -> board.getId().equals(boardId))
                || (invite != null && invite.getAccess().equalsIgnoreCase("write"))) {
                Board board1 = boardRepo.findById(boardId).orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
                if (visibility.getVisibility().equalsIgnoreCase("private")) {
                    board1.setVisibility("private");
                }else if(visibility.getVisibility().equalsIgnoreCase("public")) {
                    board1.setVisibility("public");
                }else{
                    System.out.println("bad request");
                    return "bad request";
                }
                return boardRepo.save(board1);
            }
        List<Board> boards = boardRepo.findAll();
        for (Board board : boards){
            if (boardId.equals(board.getId())){
                return "You do not have permission to change board visibility mode";
            }
        }
        return "There is a problem. Please try again later";
    }
    public Boolean checkUsernameAndOwnerId(String token,String BoardId){
        String name = userService.GetUserName(token);
        Board Board = boardRepo.findById(BoardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        System.out.println(name);
        System.out.println(Board.getOwnerId());
        return Board.getOwnerId().equals(name);
    }
    public Boolean checkBoardPublicOrPrivate(String BoardId){
        Board Board = boardRepo.findById(BoardId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if(Board.getVisibility().equalsIgnoreCase("public")){
            return true;
        }else if(Board.getVisibility().equalsIgnoreCase("private")){
            return false;
        }
        System.out.println("null");
        return null;
    }
}