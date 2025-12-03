package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        assertEquals(12, movies.size()); // Based on the movies.json file
    }

    @Test
    public void testGetMovieById() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals("The Prison Escape", movie.get().getMovieName());
        assertEquals("John Director", movie.get().getDirector());
        assertEquals("Drama", movie.get().getGenre());
    }

    @Test
    public void testGetMovieByIdNotFound() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdInvalid() {
        Optional<Movie> movie1 = movieService.getMovieById(null);
        assertFalse(movie1.isPresent());
        
        Optional<Movie> movie2 = movieService.getMovieById(-1L);
        assertFalse(movie2.isPresent());
        
        Optional<Movie> movie3 = movieService.getMovieById(0L);
        assertFalse(movie3.isPresent());
    }

    // Search functionality tests
    @Test
    public void testSearchMoviesByName() {
        List<Movie> results = movieService.searchMovies("Prison", null, null);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        List<Movie> results = movieService.searchMovies("PRISON", null, null);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNamePartialMatch() {
        List<Movie> results = movieService.searchMovies("The", null, null);
        assertNotNull(results);
        assertTrue(results.size() > 1); // Multiple movies start with "The"
        
        // Verify all results contain "The" in the name
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
        }
    }

    @Test
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 2L, null);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Family Boss", results.get(0).getMovieName());
        assertEquals(2L, results.get(0).getId());
    }

    @Test
    public void testSearchMoviesByGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Verify all results have Drama genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    public void testSearchMoviesByGenreCaseInsensitive() {
        List<Movie> results = movieService.searchMovies(null, null, "DRAMA");
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Verify all results have Drama genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    public void testSearchMoviesByMultipleCriteria() {
        List<Movie> results = movieService.searchMovies("Family", 2L, "Crime/Drama");
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Family Boss", results.get(0).getMovieName());
        assertEquals(2L, results.get(0).getId());
        assertEquals("Crime/Drama", results.get(0).getGenre());
    }

    @Test
    public void testSearchMoviesNoResults() {
        List<Movie> results = movieService.searchMovies("NonExistentMovie", null, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesWithEmptyName() {
        List<Movie> results = movieService.searchMovies("", null, null);
        assertNotNull(results);
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesWithWhitespaceName() {
        List<Movie> results = movieService.searchMovies("   ", null, null);
        assertNotNull(results);
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesWithEmptyGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "");
        assertNotNull(results);
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesWithWhitespaceGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "   ");
        assertNotNull(results);
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesWithInvalidId() {
        List<Movie> results = movieService.searchMovies(null, -1L, null);
        assertNotNull(results);
        assertEquals(12, results.size()); // Should return all movies (invalid ID ignored)
    }

    @Test
    public void testSearchMoviesWithZeroId() {
        List<Movie> results = movieService.searchMovies(null, 0L, null);
        assertNotNull(results);
        assertEquals(12, results.size()); // Should return all movies (invalid ID ignored)
    }

    @Test
    public void testSearchMoviesAllParametersNull() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        assertNotNull(results);
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesSpecificGenres() {
        // Test Action/Sci-Fi genre
        List<Movie> sciFiResults = movieService.searchMovies(null, null, "Action/Sci-Fi");
        assertNotNull(sciFiResults);
        assertFalse(sciFiResults.isEmpty());
        
        // Test Adventure/Fantasy genre
        List<Movie> fantasyResults = movieService.searchMovies(null, null, "Adventure/Fantasy");
        assertNotNull(fantasyResults);
        assertFalse(fantasyResults.isEmpty());
        
        // Test Drama/History genre
        List<Movie> historyResults = movieService.searchMovies(null, null, "Drama/History");
        assertNotNull(historyResults);
        assertFalse(historyResults.isEmpty());
    }

    @Test
    public void testSearchMoviesNameAndGenreCombination() {
        List<Movie> results = movieService.searchMovies("Space", null, "Adventure/Sci-Fi");
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Space Wars: The Beginning", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesIdAndGenreCombination() {
        List<Movie> results = movieService.searchMovies(null, 10L, "Adventure/Sci-Fi");
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Space Wars: The Beginning", results.get(0).getMovieName());
        assertEquals(10L, results.get(0).getId());
    }

    @Test
    public void testSearchMoviesConflictingCriteria() {
        // Search for a movie with ID 1 but genre that doesn't match
        List<Movie> results = movieService.searchMovies(null, 1L, "Action/Sci-Fi");
        assertNotNull(results);
        assertTrue(results.isEmpty()); // Should return empty as ID 1 is Drama, not Action/Sci-Fi
    }
}