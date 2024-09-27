package com.itbangmodkradankanbanapi.database1.repositories;

import com.itbangmodkradankanbanapi.database1.entities.Board;
import com.itbangmodkradankanbanapi.database1.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepo extends JpaRepository<Board, String> {
    List<Board> findAllByOwnerId(String name);
}
