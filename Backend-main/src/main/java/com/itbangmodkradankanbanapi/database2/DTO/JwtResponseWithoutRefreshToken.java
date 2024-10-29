package com.itbangmodkradankanbanapi.database2.DTO;

import lombok.Data;

@Data
public class JwtResponseWithoutRefreshToken {
    String access_token;
    public JwtResponseWithoutRefreshToken(String token){
        this.access_token = token;
    }
}