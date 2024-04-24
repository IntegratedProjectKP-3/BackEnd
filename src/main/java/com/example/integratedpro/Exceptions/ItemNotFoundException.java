package com.example.integratedpro.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ItemNotFoundException extends ResponseStatusException {
    public ItemNotFoundException(String massage){
        super(HttpStatus.NOT_FOUND,massage);
    }
}
