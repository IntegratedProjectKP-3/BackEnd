package com.itbangmodkradankanbanapi.database1.repositories;

import com.itbangmodkradankanbanapi.database1.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepo extends JpaRepository<Task, Integer> {
    List<Task> findAllByBoardId (String boardId);
}
