package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        return "movies";
    }

    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Ahoy! Searching for treasure with criteria - name: {}, id: {}, genre: {}", name, id, genre);
        
        try {
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            // Add search results and parameters to model
            model.addAttribute("movies", searchResults);
            model.addAttribute("searchName", name);
            model.addAttribute("searchId", id);
            model.addAttribute("searchGenre", genre);
            model.addAttribute("isSearchResult", true);
            
            // Add pirate-themed messages based on results
            if (searchResults.isEmpty()) {
                model.addAttribute("searchMessage", "Arrr! No treasure found matching yer search, matey! Try charting a different course.");
            } else {
                model.addAttribute("searchMessage", 
                    String.format("Ahoy! Found %d piece%s of cinematic treasure for ye, me hearty!", 
                        searchResults.size(), searchResults.size() == 1 ? "" : "s"));
            }
            
            logger.info("Search completed successfully. Found {} movies", searchResults.size());
            return "movies";
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid search parameters provided: {}", e.getMessage());
            model.addAttribute("title", "Arrr! Invalid Search Parameters");
            model.addAttribute("message", "Blimey! " + e.getMessage() + " Check yer search criteria, matey!");
            return "error";
        } catch (MovieSearchException e) {
            logger.error("Movie search operation failed: {}", e.getMessage(), e);
            model.addAttribute("title", "Arrr! Search Failed");
            model.addAttribute("message", "Blimey! " + e.getMessage() + " Try again, matey!");
            return "error";
        } catch (RuntimeException e) {
            logger.error("Unexpected error occurred during movie search: {}", e.getMessage(), e);
            model.addAttribute("title", "Arrr! Unexpected Trouble");
            model.addAttribute("message", "Blimey! Something unexpected went wrong while searching for yer treasure. The ship's crew be working on it, matey!");
            return "error";
        }
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }
}