package com.itbangmodkradankanbanapi.Jwt.filters;

public class MalformedJsonException extends RuntimeException {
    public MalformedJsonException(String message) {
        super(message);
    }
}
