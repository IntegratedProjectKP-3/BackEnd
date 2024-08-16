package com.itbangmodkradankanbanapi.database2.repositories;

import com.itbangmodkradankanbanapi.database2.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Integer> {


}
