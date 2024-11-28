package com.group16.tetris.utils;

// Custom exception for handling JSON file errors
public class JsonFileException extends RuntimeException {

    // Constructor to pass error message and cause to the parent exception
    public JsonFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
