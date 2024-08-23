package com.itbangmodkradankanbanapi.database2.service;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;
}
