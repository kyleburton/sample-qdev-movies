package com.amazonaws.samples.qdevmovies.movies;

/**
 * Custom exception for movie search operations.
 * Thrown when there are issues during movie search functionality.
 */
public class MovieSearchException extends RuntimeException {
    
    public MovieSearchException(String message) {
        super(message);
    }
    
    public MovieSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}