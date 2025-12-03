package com.amazonaws.samples.qdevmovies.movies;

/**
 * Custom exception for movie service operations.
 * Thrown when there are issues with movie service functionality.
 */
public class MovieServiceException extends RuntimeException {
    
    public MovieServiceException(String message) {
        super(message);
    }
    
    public MovieServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}