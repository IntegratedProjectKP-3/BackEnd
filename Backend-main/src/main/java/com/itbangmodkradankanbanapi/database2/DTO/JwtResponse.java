package com.itbangmodkradankanbanapi.database2.DTO;

import lombok.Data;

@Data
public class JwtResponse {
    String token;

    public JwtResponse(String token) {
        this.token = token;
    }
}