package com.itbangmodkradankanbanapi.database1.repositories;

import com.itbangmodkradankanbanapi.database1.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRepo extends JpaRepository<Status, Integer> {
    List<Status> findAllByNameIgnoreCase(String name);
    List<Status> findAllByBoardId(String boardId);


}
