package com.amazonaws.samples.qdevmovies.movies;

/**
 * Custom exception for movie data loading operations.
 * Thrown when there are issues loading movie data from external sources.
 */
public class MovieDataLoadException extends RuntimeException {
    
    public MovieDataLoadException(String message) {
        super(message);
    }
    
    public MovieDataLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}