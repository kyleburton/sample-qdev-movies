package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    public String getMovieDetails(@PathVariable("id") Long movieId,
                                 org.springframework.ui.Model model,
                                 HttpSession session,
                                 @RequestParam(value = "error", required = false) String error) {
        logger.info("Fetching details for movie ID: {}", movieId);

        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }

        Movie movie = movieOpt.get();

        // Get user's reviews from session
        String sessionKey = "movie_" + movieId + "_reviews";
        @SuppressWarnings("unchecked")
        List<Review> userReviews = session != null ? (List<Review>) session.getAttribute(sessionKey) : null;
        if (userReviews == null) {
            userReviews = new ArrayList<>();
            logger.info("No user reviews found for movie {}", movieId);
        } else {
            logger.info("Retrieved {} user reviews for movie {}", userReviews.size(), movieId);
        }

        // Get stored username from session
        String storedUserName = session != null ? (String) session.getAttribute("user_name") : null;

        // Combine mock reviews with user reviews using our optimized ReviewService
        List<Review> mockReviews = reviewService.getReviewsForMovie(movie.getId());
        List<Review> allReviews = new ArrayList<>();
        allReviews.addAll(mockReviews);
        allReviews.addAll(userReviews);

        // Add data to model for template
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", allReviews);
        model.addAttribute("storedUserName", storedUserName);
        model.addAttribute("error", error);

        return "movie-details";
    }

    @PostMapping("/movies/{id}/details")
    public String addReview(@PathVariable("id") Long movieId,
    public String addReview(@PathVariable("id") Long movieId,
                           @RequestParam("userName") @Valid @Size(min=2, max=50) String userName,
                           @RequestParam("rating") @Min(1) @Max(5) int rating,
                           @RequestParam("comment") @Valid @Size(min=10, max=500) String comment,
                           HttpSession session) {
        logger.info("Adding review for movie ID: {}", movieId);

        // Use our optimized MovieService instead of static array
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            return "redirect:/movies/" + movieId + "/details?error=Movie+Not+Found";
        }

        // Create review request and validate using the intentional code smells
        ReviewRequest request = new ReviewRequest(userName, rating, comment);
        String validationError = ReviewValidator.validateReview(request);

        if (validationError != null) {
            // Redirect back with error parameter
            return "redirect:/movies/" + movieId + "/details?error=" + validationError.replace(" ", "+");
        }

        // Get or create avatar for this session
        String avatar = (String) session.getAttribute("user_avatar");
        if (avatar == null) {
            // Randomly select an avatar emoji
            String[] avatars = {"👨", "👩", "🧑", "👴", "👵", "🧒"};
            Random random = new Random();
            avatar = avatars[random.nextInt(avatars.length)];
            session.setAttribute("user_avatar", avatar);
        }

        // Store username in session for future reviews
        session.setAttribute("user_name", userName);

        // Create new review
        Review newReview = new Review(userName, avatar, (double) rating, comment);

        // Get existing user reviews from session
        String sessionKey = "movie_" + movieId + "_reviews";
        @SuppressWarnings("unchecked")
        List<Review> userReviews = (List<Review>) session.getAttribute(sessionKey);
        if (userReviews == null) {
            userReviews = new ArrayList<>();
            logger.info("Creating new review list for movie {}", movieId);
        } else {
            logger.info("Found {} existing reviews for movie {}", userReviews.size(), movieId);
        }

        // Add new review to the list
        userReviews.add(newReview);
        session.setAttribute(sessionKey, userReviews);
        logger.info("Added review. Total reviews for movie {}: {}", movieId, userReviews.size());

        // Redirect back to details page (Post-Redirect-Get pattern)
        return "redirect:/movies/" + movieId + "/details?reviewAdded=true";
    }
}
