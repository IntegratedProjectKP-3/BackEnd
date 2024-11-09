package com.itbangmodkradankanbanapi.database1.repositories;

import com.itbangmodkradankanbanapi.database1.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepo extends JpaRepository<UserInfo, String> {
    UserInfo findByEmail(String email);
}
