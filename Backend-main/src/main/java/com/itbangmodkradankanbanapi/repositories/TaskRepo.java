package com.itbangmodkradankanbanapi.repositories;

import com.itbangmodkradankanbanapi.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepo extends JpaRepository<Task, Integer> {
}
