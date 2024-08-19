package com.itbangmodkradankanbanapi.database2.service;

import com.itbangmodkradankanbanapi.database2.entities.AuthUser;
import com.itbangmodkradankanbanapi.database2.entities.User;
import com.itbangmodkradankanbanapi.database2.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(userName);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, userName + " does not exist !!");
        }

        UserDetails userDetails = new AuthUser(userName, user.getPassword());
        return userDetails;
    }
}

