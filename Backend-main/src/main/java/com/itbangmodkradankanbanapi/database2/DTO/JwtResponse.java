package com.itbangmodkradankanbanapi.database2.DTO;

import lombok.Data;

@Data
public class JwtResponse {
    String access_token;
    String refresh_token;
    public JwtResponse(String token){
        this.access_token = token;
    }
    public JwtResponse(String token, String refreshToken) {
        this.access_token = token;
        this.refresh_token = refreshToken;
    }
}