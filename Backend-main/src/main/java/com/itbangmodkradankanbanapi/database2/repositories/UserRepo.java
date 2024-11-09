package com.itbangmodkradankanbanapi.database2.repositories;

import com.itbangmodkradankanbanapi.database2.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {
    User findByUsername(String name);
    User findByEmail(String email);
}
