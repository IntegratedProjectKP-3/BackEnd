package com.itbangmodkradankanbanapi.repositories;

import com.itbangmodkradankanbanapi.entities.Status;
import com.itbangmodkradankanbanapi.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusRepo extends JpaRepository<Status, Integer> {
}
