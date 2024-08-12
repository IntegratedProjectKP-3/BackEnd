package com.itbangmodkradankanbanapi.repositories;

import com.itbangmodkradankanbanapi.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRepo extends JpaRepository<Status, Integer> {
    List<Status> findAllByNameIgnoreCase(String name);



}
