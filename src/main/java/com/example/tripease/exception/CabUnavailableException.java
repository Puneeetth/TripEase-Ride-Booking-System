package com.example.tripease.exception;

public class CabUnavailableException extends RuntimeException {
    public CabUnavailableException(String message) {
        super(message);
    }
}
