package com.itbangmodkradankanbanapi.database2.DTO;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserDTO {
    @Column(nullable = false, name = "email")
    private String email;
}
