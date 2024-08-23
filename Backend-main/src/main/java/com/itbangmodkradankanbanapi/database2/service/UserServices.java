package com.itbangmodkradankanbanapi.database2.service;

import com.itbangmodkradankanbanapi.database2.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServices implements UserDetailsService {
    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
//        return repo.findByUsername(userName).orElseThrow();
        return (UserDetails) repo.findByUsername(userName);


//        return (UserDetails) repo.findByUsername(userName).orElseThrow();
//        return (UserDetails) repo.findByUsername(userName);
    }




}
