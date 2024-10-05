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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class BoardAndTaskServices {
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
    private UserInfoRepo userInfoRepo;
    public List<HomePageTaskDTO> getPrivateTask(String BoardId,String token){
        List<Board> OwnBoard = showOwnBoard(token);
        System.out.println("board Id : " + BoardId);
        for (Board board : OwnBoard) {
            if(Objects.equals(board.getId(), BoardId)){
                if (taskRepo.findAllByBoardId(BoardId) == null || taskRepo.findAllByBoardId(BoardId).isEmpty()){
                    return new ArrayList<>();
                }
                return listMapper.mapList(taskRepo.findAllByBoardId(BoardId),HomePageTaskDTO.class,modelMapper);
            }
            System.out.println(board.getId());
        }
        return null;
    }
    public TaskDTO3_V2  addPrivateTask(TaskDTO3_V2_addTask newTask,String boardId,String token){
        List<Board> OwnBoard = showOwnBoard(token);
        System.out.println("Board Id : " +  boardId);
        for (Board board : OwnBoard) {
            System.out.println(board.getId());
            if(Objects.equals(board.getId(),boardId)){
                Status statusObject = statusRepo.findById(newTask.getStatus()).orElseThrow(()-> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
                if (Objects.equals(statusObject.getBoardId(), boardId) && !Objects.equals(statusObject.getBoardId(), "public")){
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
                Task existingTask = taskRepo.findById(TaskId).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
                existingTask.setDescription(newTask.getDescription());
                existingTask.setTitle(newTask.getTitle());
                existingTask.setAssignees(newTask.getAssignees());
                Status statusObject = statusRepo.findById(newTask.getStatus()).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
                existingTask.setStatus(statusObject);
                existingTask.setBoardId(boardId);
                Task task = taskRepo.save(existingTask);
                return modelMapper.map(task, TaskDTO3_V2.class);
    }
public TaskDTO3_V2  deletePrivateTask(Integer TaskId,String boardId,String token) {
    List<Board> OwnBoard = showOwnBoard(token);
    for (Board board : OwnBoard) {
        if (Objects.equals(board.getId(), boardId)) {
            Task task =  taskRepo.findById(TaskId).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
            TaskDTO3_V2 taskDTO = modelMapper.map(task, TaskDTO3_V2.class);
            taskRepo.delete(task);
            return taskDTO;
        }
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
        Board newBoard = new Board(newId, name.getName(), userService.GetUserName(token), null, null);
        for(int i=1; i < 5;i++){
            Status status = statusRepo.findById(i).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
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
    public Board getBoardDetail(String token,String boardId){
        List<Board> OwnBoard = showOwnBoard(token);
        for (Board board : OwnBoard) {
            if (board.getId().equals(boardId)) {
                return boardRepo.findById(boardId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"TaskId does not exist !!!"));
            }
        }
        return null;
    }
    public Task getFullTask(Integer id,String boardId,String token){
        List<Board> OwnBoard = showOwnBoard(token);
        for (Board board : OwnBoard) {
            if (board.getId().equals(boardId)) { // Comparing boardId with the board's ID
                return taskRepo.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"TaskId does not exist !!!"));
            }}
        System.out.println("null");
        return null;
    }
    //    public List<HomePageTaskDTO> getAllTask(){
//        return listMapper.mapList(taskRepo.findAll(),HomePageTaskDTO.class,modelMapper);
//    }
//    public Task findId(Integer Id){
//        return taskRepo.findById(Id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"TaskId "+Id + " does not exist !!!"));
//    }
//
//    @Transactional
//    public TaskDTO3_V2 addTask(TaskDTO3_V2_addTask newTask){
//        Status statusObject = statusRepo.findById(newTask.getStatus()).orElseThrow(()-> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
//        Task task = modelMapper.map(newTask, Task.class);
//        task.setStatus(statusObject);
//        Task savedTask = taskRepo.saveAndFlush(task);
//        return modelMapper.map(savedTask, TaskDTO3_V2.class);
//    }

// not use
//    public boolean deleteTask(Integer Id){
//        try {
//            taskRepo.deleteById(Id);
//            return true;
//        }catch (EmptyResultDataAccessException e) {
//            return false;
//        }
//    }

//    @Transactional
//    public TaskDTO2 deleteTask(int id){
//        Task task =  taskRepo.findById(id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
//        TaskDTO2 taskDTO = modelMapper.map(task, TaskDTO2.class);
//        taskRepo.delete(task);
//        taskDTO.setDescription(null);
//        return taskDTO;
//    }
//
//    @Transactional
//    public TaskDTO2 updateTask(Integer id, TaskDTO2 taskDTO2) {
//        Task existingTask = taskRepo.findById(id).orElseThrow(
//                () -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
//        existingTask.setDescription(taskDTO2.getDescription());
//        existingTask.setTitle(taskDTO2.getTitle());
//        existingTask.setAssignees(taskDTO2.getAssignees());
//        existingTask.setStatus(taskDTO2.getStatus());
//        Task savedTask = taskRepo.save(existingTask);
//        TaskDTO2 updateTaskDTO = modelMapper.map(savedTask, TaskDTO2.class);
//        updateTaskDTO.setDescription(null);
//        return  updateTaskDTO;
//    }
//
//
}