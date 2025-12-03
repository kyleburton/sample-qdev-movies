package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> getAllMovies() {
                return Arrays.asList(
                    new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                    new Movie(2L, "Action Movie", "Action Director", 2022, "Action", "Action description", 110, 4.0),
                    new Movie(3L, "Comedy Movie", "Comedy Director", 2021, "Comedy", "Comedy description", 95, 3.5)
                );
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                if (id == 1L) {
                    return Optional.of(new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5));
                }
                return Optional.empty();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> allMovies = getAllMovies();
                List<Movie> results = new ArrayList<>();
                
                for (Movie movie : allMovies) {
                    boolean matches = true;
                    
                    if (name != null && !name.trim().isEmpty()) {
                        matches = matches && movie.getMovieName().toLowerCase().contains(name.toLowerCase());
                    }
                    
                    if (id != null && id > 0) {
                        matches = matches && movie.getId() == id;
                    }
                    
                    if (genre != null && !genre.trim().isEmpty()) {
                        matches = matches && movie.getGenre().toLowerCase().equals(genre.toLowerCase());
                    }
                    
                    if (matches) {
                        results.add(movie);
                    }
                }
                
                return results;
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
    }

    @Test
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model, null, null);
        assertNotNull(result);
        assertEquals("movie-details", result);
    }

    @Test
    public void testAddReview() {
        // Mock HttpSession
        javax.servlet.http.HttpSession mockSession = org.mockito.Mockito.mock(javax.servlet.http.HttpSession.class);
        
        String result = moviesController.addReview(1L, "Test User", 5, "Great movie!", mockSession);
        assertNotNull(result);
        assertEquals("redirect:/movies/1/details?reviewAdded=true", result);
    }

    @Test
    public void testAddReviewInvalidMovie() {
        javax.servlet.http.HttpSession mockSession = org.mockito.Mockito.mock(javax.servlet.http.HttpSession.class);
        
        String result = moviesController.addReview(999L, "Test User", 5, "Great movie!", mockSession);
        assertNotNull(result);
        assertEquals("redirect:/movies/999/details?error=Movie+Not+Found", result);
    }

    @Test
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model, null, null);
        assertNotNull(result);
        assertEquals("error", result);
    }

    @Test
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    // New tests for search functionality
    @Test
    public void testSearchMoviesWithName() {
        String result = moviesController.searchMovies("Test", null, null, model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
        
        assertTrue((Boolean) model.getAttribute("isSearchResult"));
        assertNotNull(model.getAttribute("searchMessage"));
    }

    @Test
    public void testSearchMoviesWithId() {
        String result = moviesController.searchMovies(null, 2L, null, model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals("Action Movie", movies.get(0).getMovieName());
        
        assertTrue((Boolean) model.getAttribute("isSearchResult"));
        assertEquals(2L, model.getAttribute("searchId"));
    }

    @Test
    public void testSearchMoviesWithGenre() {
        String result = moviesController.searchMovies(null, null, "Comedy", model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals("Comedy Movie", movies.get(0).getMovieName());
        
        assertTrue((Boolean) model.getAttribute("isSearchResult"));
        assertEquals("Comedy", model.getAttribute("searchGenre"));
    }

    @Test
    public void testSearchMoviesWithMultipleCriteria() {
        String result = moviesController.searchMovies("Action", 2L, "Action", model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals("Action Movie", movies.get(0).getMovieName());
        
        assertTrue((Boolean) model.getAttribute("isSearchResult"));
        assertEquals("Action", model.getAttribute("searchName"));
        assertEquals(2L, model.getAttribute("searchId"));
        assertEquals("Action", model.getAttribute("searchGenre"));
    }

    @Test
    public void testSearchMoviesNoResults() {
        String result = moviesController.searchMovies("NonExistent", null, null, model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertTrue(movies.isEmpty());
        
        assertTrue((Boolean) model.getAttribute("isSearchResult"));
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertNotNull(searchMessage);
        assertTrue(searchMessage.contains("No treasure found"));
    }

    @Test
    public void testSearchMoviesWithInvalidId() {
        String result = moviesController.searchMovies(null, -1L, null, model);
        
        assertNotNull(result);
        assertEquals("error", result);
        
        String title = (String) model.getAttribute("title");
        String message = (String) model.getAttribute("message");
        assertNotNull(title);
        assertNotNull(message);
        assertTrue(title.contains("Invalid Search Parameters"));
        assertTrue(message.contains("not a valid treasure map number"));
    }

    @Test
    public void testSearchMoviesWithEmptyParameters() {
        String result = moviesController.searchMovies("", null, "", model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(3, movies.size()); // Should return all movies when no valid criteria provided
        
        assertTrue((Boolean) model.getAttribute("isSearchResult"));
    }

    @Test
    public void testSearchMoviesCaseInsensitive() {
        String result = moviesController.searchMovies("TEST", null, null, model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals("Test Movie", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesPartialNameMatch() {
        String result = moviesController.searchMovies("Act", null, null, model);
        
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(1, movies.size());
        assertEquals("Action Movie", movies.get(0).getMovieName());
    }
}
