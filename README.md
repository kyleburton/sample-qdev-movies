# Movie Service - Spring Boot Demo Application

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a fun pirate-themed search interface.

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **🏴‍☠️ Pirate-Themed Movie Search**: Hunt for cinematic treasure using our swashbuckling search interface
- **Advanced Filtering**: Search by movie name, ID, or genre with partial matching and case-insensitive search
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Log4j 2.20.0**
- **JUnit 5.8.2**
- **Thymeleaf** for templating

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **🏴‍☠️ Movie Search**: http://localhost:8080/movies/search (with query parameters)

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── MoviesApplication.java    # Main Spring Boot application
│   │       ├── MoviesController.java     # REST controller for movie endpoints
│   │       ├── Movie.java                # Movie data model
│   │       ├── MovieService.java         # Business logic with search functionality
│   │       ├── Review.java               # Review data model
│   │       └── utils/
│   │           ├── HTMLBuilder.java      # HTML generation utilities
│   │           └── MovieUtils.java       # Movie validation utilities
│   └── resources/
│       ├── application.yml               # Application configuration
│       ├── movies.json                   # Movie data source
│       ├── mock-reviews.json             # Mock review data
│       ├── templates/
│       │   ├── movies.html               # Movie list with pirate search form
│       │   └── movie-details.html        # Movie details page
│       └── log4j2.xml                    # Logging configuration
└── test/                                 # Unit tests with comprehensive search coverage
```

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings and basic information, including a pirate-themed search form.

### 🏴‍☠️ Search for Treasure (Movies)
```
GET /movies/search
```
Ahoy matey! Search for cinematic treasure using our advanced filtering system.

**Query Parameters:**
- `name` (optional): Movie name to search for (partial match, case-insensitive)
- `id` (optional): Movie ID to search for (exact match, must be positive integer)
- `genre` (optional): Movie genre to search for (exact match, case-insensitive)

**Search Logic:**
- Multiple parameters are combined with AND logic
- Name searches support partial matching (e.g., "Prison" matches "The Prison Escape")
- All text searches are case-insensitive
- Empty or whitespace-only parameters are ignored
- Invalid IDs (negative or zero) return error responses

**Examples:**
```bash
# Search by movie name (partial match)
http://localhost:8080/movies/search?name=Prison

# Search by specific treasure map ID
http://localhost:8080/movies/search?id=5

# Search by adventure type (genre)
http://localhost:8080/movies/search?genre=Drama

# Combine multiple search criteria
http://localhost:8080/movies/search?name=Family&genre=Crime/Drama

# Case-insensitive search
http://localhost:8080/movies/search?name=SPACE&genre=adventure/sci-fi
```

**Response:**
Returns the same movies.html template with:
- Filtered movie results
- Pirate-themed success/error messages
- Search form pre-populated with search criteria
- Navigation options to return to full treasure chest

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

## 🏴‍☠️ Pirate Search Features

### Search Interface
- **Treasure Chest Design**: Pirate-themed search form with gold and brown styling
- **Nautical Labels**: "Treasure Map ID", "Adventure Type (Genre)", etc.
- **Pirate Buttons**: "Hunt for Treasure!", "Show All Treasure"
- **Responsive Design**: Works on all devices from desktop to mobile

### Search Results
- **Success Messages**: "Ahoy! Found X pieces of cinematic treasure for ye, me hearty!"
- **Empty Results**: "Arrr! No treasure found matching yer search, matey!"
- **Error Handling**: "Blimey! That ID be not a valid treasure map number!"
- **Navigation**: Easy return to full movie catalog

### Available Genres for Search
- Drama
- Crime/Drama
- Action/Crime
- Drama/Romance
- Action/Sci-Fi
- Adventure/Fantasy
- Adventure/Sci-Fi
- Drama/History
- Drama/Thriller

## Testing

Run the comprehensive test suite:
```bash
mvn test
```

### Test Coverage
- **MovieService Tests**: Complete coverage of search functionality including edge cases
- **Controller Tests**: Full integration testing of search endpoints with pirate messaging
- **Edge Case Testing**: Invalid parameters, empty results, case sensitivity, partial matching
- **Error Handling**: Validation of error responses and pirate-themed error messages

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Verify the application is running on the correct port
2. Check that movie data is loaded (should see 12 movies in catalog)
3. Ensure search parameters are properly URL-encoded
4. Check application logs for any error messages

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog
- Enhance the pirate theme with additional nautical elements
- Add new search features like rating-based filtering
- Improve the responsive design
- Add more comprehensive error handling
- Extend the pirate language vocabulary

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*Arrr! May fair winds fill yer sails as ye navigate this cinematic treasure trove, matey! 🏴‍☠️⚓*
