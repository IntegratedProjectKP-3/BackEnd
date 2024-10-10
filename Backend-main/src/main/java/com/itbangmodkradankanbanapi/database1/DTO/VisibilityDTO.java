package com.itbangmodkradankanbanapi.database1.DTO;


import jakarta.persistence.Column;
import lombok.Data;

@Data
public class VisibilityDTO {
    @Column(nullable = false, name = "name",length = 120)
    private String visibility;
}
