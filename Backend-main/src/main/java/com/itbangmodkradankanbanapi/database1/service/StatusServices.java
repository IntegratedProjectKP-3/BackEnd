package com.itbangmodkradankanbanapi.database1.service;

import com.itbangmodkradankanbanapi.database1.DTO.StatusDTO;
import com.itbangmodkradankanbanapi.database1.DTO.TaskDTO3_V2;
import com.itbangmodkradankanbanapi.database1.DTO.TaskDTO3_V2_addTask;
import com.itbangmodkradankanbanapi.database1.entities.Board;
import com.itbangmodkradankanbanapi.database1.entities.Status;
import com.itbangmodkradankanbanapi.database1.entities.Task;
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
    public static <Z, X> List<X> mapList(List<Z> sourceList, Class<X> destinationType) {
        ModelMapper modelMapper = new ModelMapper();
        return sourceList.stream()
                .map(element -> modelMapper.map(element, destinationType))
                .collect(Collectors.toList());
    }

    public Status findId(Integer Id){
        return statusRepo.findById(Id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"status "+Id + " does not exist !!!"));
    }

    public List<StatusDTO> findPrivateTask(String token,String boardId) {
        List<Board> OwnBoard = boardAndTaskServices.showOwnBoard(token);
        for (Board board : OwnBoard) {
            if (Objects.equals(board.getId(), boardId)) {
               List<Status> status =  statusRepo.findAllByBoardId(boardId);
               List<StatusDTO> statusDTOS = mapList(status,StatusDTO.class);
               return statusDTOS;
           }
        }
        return null;
    }
    public List<StatusDTO> getAllStatus(){
        List<Status> status = statusRepo.findAll();
        List<StatusDTO> statusDTOS = mapList(status,StatusDTO.class);
        return statusDTOS;
    }

    @Transactional
    public StatusDTO addStatus(StatusDTO newStatus,String token,String boardId) {
        List<Board> OwnBoard = boardAndTaskServices.showOwnBoard(token);
        for (Board board : OwnBoard) {
            if (Objects.equals(board.getId(), boardId)) {
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
        }
        return null;
    }
    @Transactional
    public StatusDTO updateStatus(Integer id, StatusDTO status,String token,String boardId) {
        List<Board> OwnBoard = boardAndTaskServices.showOwnBoard(token);
        for (Board board : OwnBoard) {
            if (Objects.equals(board.getId(), boardId)) {
                Status currentStatus = statusRepo.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"status does not exist !!!"));
                currentStatus.setName(status.getName().trim());
                if (status.getDescription()!= null){
                    currentStatus.setDescription(status.getDescription());
                }
                System.out.println("before save");
                statusRepo.saveAndFlush(currentStatus);
                System.out.println("after save");
                System.out.println(modelMapper.map(currentStatus, StatusDTO.class));
                System.out.println("after save1");
                return modelMapper.map(currentStatus, StatusDTO.class);
            }
        }
        return null;
    }

    @Transactional
    public void deleteStatus(Integer id,String token,String boardId) {
        List<Board> OwnBoard = boardAndTaskServices.showOwnBoard(token);
        for (Board board : OwnBoard) {
            if (Objects.equals(board.getId(), boardId)) {
                Status status = statusRepo.findById(id).orElseThrow(() -> new ItemNotFoundForUpdateAndDelete("NOT FOUND"));
                statusRepo.delete(status);
            }
        }
    }

    @Transactional
    public void deleteStatusAndTransfer(Integer id, Integer newId,String boardId,String token) {
        List<Board> OwnBoard = boardAndTaskServices.showOwnBoard(token);
        for (Board board : OwnBoard) {
            if (Objects.equals(board.getId(), boardId)) {
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
    }
}
