package com.itbangmodkradankanbanapi.database1.service;

import com.itbangmodkradankanbanapi.database1.DTO.*;
import com.itbangmodkradankanbanapi.database1.entities.Board;
import com.itbangmodkradankanbanapi.database1.entities.Status;
import com.itbangmodkradankanbanapi.database1.entities.Task;
import com.itbangmodkradankanbanapi.database1.repositories.BoardRepo;
import com.itbangmodkradankanbanapi.database1.repositories.StatusRepo;
import com.itbangmodkradankanbanapi.database1.repositories.TaskRepo;
import com.itbangmodkradankanbanapi.database1.repositories.UserInfoRepo;
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
    public Object getPrivateTask(String BoardId,String token){
        Board board1 = boardRepo.findById(BoardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
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
        List<Board> OwnBoard = showOwnBoard(token);
        if (OwnBoard.stream().anyMatch(board -> board.getId().equals(BoardId)) || board1.getVisibility().equals("public")) {
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
        List<Board> OwnBoard = showOwnBoard(token);
        System.out.println("Board Id : " +  boardId);
        for (Board board : OwnBoard) {
            System.out.println(board.getId());
            if(Objects.equals(board.getId(),boardId)){
                System.out.println("Objects.equals(board.getId(),boardId)");
                Status statusObject = statusRepo.findById(newTask.getStatus()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
                System.out.println("findById");
                System.out.println(statusObject.getBoardId());
                System.out.println(boardId);
                System.out.println(Objects.equals(statusObject.getBoardId(),boardId));
                if (Objects.equals(statusObject.getBoardId(),boardId)&& !Objects.equals(statusObject.getBoardId(), "public")){
                    System.out.println("Objects.equals(statusObject.getBoardId(), boardId) && !Objects.equals(statusObject.getBoardId(), \"public\")");
                    Task task = modelMapper.map(newTask, Task.class);
                    task.setStatus(statusObject);
                    if(!Objects.equals(task.getBoardId(), "public")){
                        task.setBoardId(boardId);
                    }
                    taskRepo.save(task);
                    return modelMapper.map(task, TaskDTO3_V2.class);
                }
            }
        }
        return null;
    }
    public TaskDTO3_V2  addPublicTask(TaskDTO3_V2_addTask newTask){
        Status statusObject = statusRepo.findById(newTask.getStatus()).orElseThrow(()-> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
        if (Objects.equals(statusObject.getBoardId(), "public")){
            Task task = modelMapper.map(newTask, Task.class);
            task.setStatus(statusObject);
            task.setBoardId("public");
            taskRepo.save(task);
            return modelMapper.map(task, TaskDTO3_V2.class);
        }
        return null;
    }
    public TaskDTO3_V2  updatePrivateTask(TaskDTO3_V2_addTask newTask,Integer TaskId,String boardId,String token) {
        Task existingTask = taskRepo.findById(TaskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskId does not exist !!!"));
        existingTask.setDescription(newTask.getDescription());
        existingTask.setTitle(newTask.getTitle());
        existingTask.setAssignees(newTask.getAssignees());
        Status statusObject = statusRepo.findById(newTask.getStatus()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "StatusId does not exist !!!"));
        existingTask.setStatus(statusObject);
        existingTask.setBoardId(boardId);
        Task task = taskRepo.save(existingTask);
        return modelMapper.map(task, TaskDTO3_V2.class);
    }
public Object  deletePrivateTask(Integer TaskId,String boardId,String token) {
    List<Board> OwnBoard = showOwnBoard(token);
    Board board1 = boardRepo.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
    if (OwnBoard.stream().anyMatch(board -> board.getId().equals(boardId)) || board1.getVisibility().equals("public")) {
            Task task =  taskRepo.findById(TaskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskId does not exist !!!"));
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
        Board newBoard = new Board(newId, name.getName(), userService.GetUserName(token), null, null,"private");
        for(int i=1; i < 5;i++){
            Status status = statusRepo.findById(i).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ฆะฟะีหณก does not exist !!!"));
            Status newStatus = new Status(null,status.getName() +" for " + newId,status.getDescription(),newId,null);
            statusRepo.save(newStatus);
        }
        boardRepo.save(newBoard);
        return newBoard;
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
        List<Board> OwnBoard = showOwnBoard(token);
        Board board1 = boardRepo.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if (OwnBoard.stream().anyMatch(board -> board.getId().equals(boardId)) || board1.getVisibility().equals("public")) {
            return boardRepo.findById(boardId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"TaskId does not exist !!!"));
        }else if(board1.getVisibility().equals("private")){
            return "You do not have permission to change board visibility mode";
        }
        return "bad request";
    }
    public Object getFullTask(Integer id,String boardId,String token){
        List<Board> OwnBoard = showOwnBoard(token);
        Board board1 = boardRepo.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if (OwnBoard.stream().anyMatch(board -> board.getId().equals(boardId)) || board1.getVisibility().equals("public")) {
                return taskRepo.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"TaskId does not exist !!!"));
        }else if(board1.getVisibility().equals("private")){
            return "403";
        }
        System.out.println("null");
        return null;
    }
    @Transactional
    public Object TogglePrivateAndPublicBoard(String boardId,String token){
        List<Board> OwnBoard = showOwnBoard(token);
        for (Board board : OwnBoard) {
            if (board.getId().equals(boardId)) {
                Board board1 = boardRepo.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
                if (board1.getVisibility().equals("private")) {
                    board1.setVisibility("public");
                }else if(board1.getVisibility().equals("public")) {
                    board1.setVisibility("private");
                }else{
                    return "bad request";
                }
                return boardRepo.save(board1);
            }
        }
        List<Board> boards = boardRepo.findAll();
        for (Board board : boards){
            if (boardId.equals(board.getId())){
                return "You do not have permission to change board visibility mode";
            }
        }
        return "There is a problem. Please try again later";
    }
    public Object findByIdAndCheckVisibility(String boardId){
        Board board1 = boardRepo.findById(boardId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "BoardId does not exist !!!"));
        if(board1.getVisibility().equals("private")){
            return "private";
        }else if(board1.getVisibility().equals("public")){
            return "public";
        }
        return null;
    }
}