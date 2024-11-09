package com.itbangmodkradankanbanapi.database1.DTO;


import jakarta.persistence.Column;
import lombok.Data;

@Data
public class InviteDTO {
    @Column(nullable = false, name = "email")
    private String email;
    @Column(nullable = false, name = "access")
    private String accessRight;
}
