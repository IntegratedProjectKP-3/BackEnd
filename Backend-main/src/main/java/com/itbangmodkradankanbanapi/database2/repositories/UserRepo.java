package com.itbangmodkradankanbanapi.database2.repositories;

import com.itbangmodkradankanbanapi.database2.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {

//    Optional<User> findByUsername(String name);

    User findByUsername(String name);

//    Optional<User> findById(Integer integer);
}
