package com.itbangmodkradankanbanapi.database2.repositories;

import com.itbangmodkradankanbanapi.database2.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String name);

    Optional<User> findById(Integer integer);
}
