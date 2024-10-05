package com.itbangmodkradankanbanapi.database2.DTO;

import lombok.Data;

@Data
public class JwtResponse {
    String access_token;

    public JwtResponse(String token) {
        this.access_token = token;
    }
}