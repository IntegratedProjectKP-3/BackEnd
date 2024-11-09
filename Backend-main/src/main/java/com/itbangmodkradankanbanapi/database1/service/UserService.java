package com.itbangmodkradankanbanapi.database1.service;

import com.itbangmodkradankanbanapi.database1.entities.UserInfo;
import com.itbangmodkradankanbanapi.database1.repositories.UserInfoRepo;
import com.itbangmodkradankanbanapi.database2.service.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class UserService {
    @Autowired
    private UserInfoRepo userInfoRepo;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    public UserInfo AddNewUser(String token) {
        String iss = (String) jwtTokenUtil.getAllClaimsFromToken(token).get("iss");
        String name = (String) jwtTokenUtil.getAllClaimsFromToken(token).get("name");
        String oid = (String) jwtTokenUtil.getAllClaimsFromToken(token).get("oid");
        String role =  (String) jwtTokenUtil.getAllClaimsFromToken(token).get("role");
        String email = (String) jwtTokenUtil.getAllClaimsFromToken(token).get("email");
        return new UserInfo(oid,name,name.substring(4),email,null,null);
    }
    public UserInfo FindUser(String token){
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // ตัด "Bearer " ออกไป
        }
        String oid = (String) jwtTokenUtil.getAllClaimsFromToken(token).get("oid");
        String finalToken = token;
        return userInfoRepo.findById(oid).orElseGet(() -> AddNewUser(finalToken));
    }
    public String GetUserName(String token){
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String name = (String) jwtTokenUtil.getAllClaimsFromToken(token).get("name");
        return name;
    }
    public String getUserEmail(String token){
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return (String) jwtTokenUtil.getAllClaimsFromToken(token).get("email");
    }
    public String getUserId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return (String) jwtTokenUtil.getAllClaimsFromToken(token).get("oid");
    }
}
