package com.itbangmodkradankanbanapi.database2.service;

import com.itbangmodkradankanbanapi.database2.DTO.UserDTO;
import com.itbangmodkradankanbanapi.database2.entities.User;
import com.itbangmodkradankanbanapi.database2.repositories.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServices implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    public static <Z, X> List<X> mapList(List<Z> sourceList, Class<X> destinationType) {
        ModelMapper modelMapper = new ModelMapper();
        return sourceList.stream()
                .map(element -> modelMapper.map(element, destinationType))
                .collect(Collectors.toList());
    }
    public Object showAllUser(){
        List<User> users =  userRepo.findAll();
        return users;
    }
    public boolean checkUserEmail(String email){
        return userRepo.findByEmail(email) != null;
    }
//    public List<?> searchByRole(String role){
//
//    }
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return (UserDetails) userRepo.findByUsername(userName);
    }
}
