package com.itbangmodkradankanbanapi.database2.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Data
public class JwtRequestUser {
    @NotBlank
    @NotNull
    @Size(max = 50)
    private String userName;
    @Size(max = 14)
    @NotBlank
    @NotNull
    private String password;
}
