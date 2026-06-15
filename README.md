# NextPlay

NextPlay is a full-stack game discovery and recommendation application built with a Spring Boot backend and a React frontend.

The goal of this project is to help users browse games, search and filter game data, save games to a personal library, track game progress, write reviews, and receive personalized game recommendations based on genres, platforms, ratings, library activity, and user reviews.

This project is being built step-by-step while relearning and practicing Spring Boot backend development, frontend development, authentication, API integration, testing, API documentation, Docker, and full-stack application structure.

---

## Project Goal

The final goal of NextPlay is to become a complete full-stack platform where users can:

- Browse games
- Search and filter games
- View game details
- Create accounts
- Log in securely
- Stay authenticated with JWT-based login
- Save games to a personal library
- Track games as want to play, playing, completed, or dropped
- Rate and review games
- View reviews for games
- View their own reviews
- Receive personalized game recommendations
- View a clean, modern frontend interface

Admin users can eventually be able to:

- Add games
- Update games
- Delete games
- Manage game information
- Moderate reviews

---

## Current Progress

### Week 1 Complete

The Week 1 goal was to build the foundation of the backend API.

Completed so far:

- Created the Spring Boot backend project
- Set up a working health check route
- Created the first game-related backend features
- Added controller, service, repository, entity, DTO, and exception layers
- Practiced the Controller → Service → Repository architecture
- Used DTOs for request and response data
- Added basic validation
- Added basic exception handling
- Tested the backend using Postman

### Week 2 Complete

The Week 2 goal was to build a search, filter, pagination, and sorting API for retrieving games.

Completed so far:

- Added pagination for game results
- Added sorting for game results
- Added filtering by title, release date, genre, and platform
- Added sample game data
- Improved validation messages
- Improved error response formatting
- Tested search, filtering, pagination, and sorting with Postman

### Week 3 Complete

The Week 3 goal was to add user authentication with Spring Security and build a personal game library feature.

Completed so far:

- Added user registration
- Added user login
- Added JWT-based authentication
- Implemented a custom JWT filter
- Protected important API endpoints
- Added a user game library feature
- Allowed users to add existing games to their library
- Allowed users to view their personal library
- Allowed users to update the status of games in their library
- Allowed users to delete games from their library
- Added custom exception handling for authentication and library features
- Tested all Week 3 endpoints using Postman

### Week 4 Complete

The Week 4 goal was to add game reviews and begin building the recommendation system.

Completed so far:

- Added review creation for games
- Required users to be authenticated before creating reviews
- Required games to exist before they can be reviewed
- Required games to be in the user's library before creating a review
- Prevented users from reviewing the same game more than once
- Added review updates for the authenticated user's own reviews
- Added review deletion for the authenticated user's own reviews
- Added paginated review results for a specific game
- Added paginated review results for the logged-in user
- Added automatic average rating updates when reviews are created, updated, or deleted
- Added transaction handling for review-related database operations
- Added recommendation logic for suggesting games to users
- Tested review and recommendation endpoints using Postman

### Week 5 Complete

The Week 5 goal was to build the frontend foundation, connect it to the backend, and create a modern user interface.

Completed so far:

- Created the React frontend for the NextPlay application
- Set up frontend routing for the main pages
- Added an authentication context to manage the logged-in user's auth state
- Added protected route logic for pages that require login
- Connected the frontend to backend authentication endpoints
- Stored and reused the JWT token for authenticated requests
- Built frontend pages for user login, registration, profile, game browsing, and user-specific features
- Connected protected frontend pages to secured backend endpoints
- Used `useEffect` for page-level data fetching where needed
- Added loading and error handling for frontend API requests
- Incorporated Tailwind CSS throughout the frontend
- Updated the application styling to look cleaner, more modern, and more polished
- Tested the frontend and backend together to make sure the application works as expected

### Week 6 Complete

The Week 6 goal was to improve project quality by adding automated tests, API documentation, and Docker support.

Completed so far:

- Added unit tests using JUnit
- Added Mockito-based service tests
- Added controller tests for API endpoints
- Tested service-layer business logic without needing the full application to run
- Tested controller behavior such as request handling, responses, and validation
- Mocked dependencies such as repositories, services, authentication, and security-related objects
- Added tests for authentication-related functionality
- Added tests for game service functionality
- Added tests for library service functionality
- Added tests for review service functionality
- Added tests for recommendation service functionality
- Added Swagger/OpenAPI documentation
- Added Swagger UI for viewing and testing API endpoints
- Added Docker support to make the project easier to run in different environments
- Improved the project structure so it is closer to being portfolio-ready

### Week 7 In Progress

The Week 7 goal is to move the project into the deployment phase and prepare NextPlay to run outside of the local development environment.

Planned tasks:

- Prepare the backend for production deployment
- Prepare the frontend for production deployment
- Configure production environment variables
- Move sensitive values such as database credentials and JWT secrets out of hardcoded configuration
- Create or improve production-ready Docker configuration
- Update Docker Compose setup for backend, frontend, and database services
- Configure the backend to connect to a production database
- Configure frontend API base URLs for deployed backend requests
- Review and update CORS settings for the deployed frontend domain
- Build the frontend for production
- Test the backend and frontend using production-like settings
- Verify Swagger/OpenAPI documentation works correctly after deployment
- Add deployment instructions to the README
- Prepare the project for deployment on a hosting platform such as Render, Railway, Fly.io, Vercel, Netlify, or another cloud provider

---

## Core Features Implemented

### Backend Features

- REST API using Spring Boot
- Game CRUD foundation
- Game search and filtering
- Pagination and sorting
- DTO-based request and response handling
- Bean validation
- Global exception handling
- User registration and login
- Spring Security authentication
- JWT token generation and validation
- Custom JWT authentication filter
- Protected backend routes
- Role-based protection for admin game management
- Personal user game library
- Game status tracking
- Review creation, updating, and deletion
- Paginated game reviews
- Paginated user reviews
- Average game rating calculation
- Basic personalized game recommendations
- Transaction handling in the service layer
- Unit testing with JUnit
- Service testing with Mockito
- Controller testing
- Swagger/OpenAPI documentation
- Docker support

### Frontend Features

- React frontend application
- Page-based frontend structure
- Client-side routing
- Login and registration pages
- Auth Context for global authentication state
- Protected routes for authenticated pages
- JWT storage and reuse for authenticated requests
- Profile page connected to the authenticated user endpoint
- Game browsing interface
- User-specific frontend pages connected to protected backend APIs
- Loading and error states for API requests
- Tailwind CSS styling
- Modernized UI design

---

## Tech Stack

### Backend

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- Bean Validation
- JUnit
- Mockito
- Swagger/OpenAPI
- Maven
- PostgreSQL or MySQL
- Docker
- Postman

### Frontend

- React
- React Router
- Tailwind CSS
- JavaScript
- Fetch API
- Local Storage for JWT persistence

---

## How to Run the Project

### 1. Clone the repository

```bash
git clone <your-repository-url>
cd nextplay
```

### 2. Configure the backend database

Update the database settings inside:

```text
src/main/resources/application.properties
```

Example PostgreSQL configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/nextplay
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Example MySQL configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/nextplay
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### 3. Run the backend

```bash
mvn spring-boot:run
```

The backend should start on:

```text
http://localhost:8080
```

### 4. Run the frontend

From the frontend folder, install dependencies and start the development server:

```bash
npm install
npm run dev
```

The frontend should start on the local development URL shown in the terminal.

### 5. Run with Docker

If Docker is configured for the project, run the application using:

```bash
docker compose up --build
```

To stop the running containers:

```bash
docker compose down
```

---

## Deployment Plan

Week 7 focuses on preparing the project for deployment.

Deployment preparation includes:

- Creating production-ready backend configuration
- Creating production-ready frontend configuration
- Using environment variables for sensitive values
- Configuring database connection settings for production
- Configuring JWT secret and token expiration settings for production
- Updating CORS settings for the deployed frontend URL
- Building the React frontend for production
- Running the backend with production-like settings
- Testing API requests against the deployed backend
- Verifying Swagger/OpenAPI documentation after deployment
- Using Docker or Docker Compose where needed
- Adding clear deployment instructions for future use

Potential deployment options include:

- Render
- Railway
- Fly.io
- Vercel
- Netlify
- Supabase or Neon for PostgreSQL hosting

---

## API Documentation

Swagger/OpenAPI documentation has been added to the backend.

When the backend is running, the Swagger UI can be used to view and test API endpoints from the browser.

Common Swagger UI URL:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## Testing

Postman is being used to manually test the backend, and JUnit/Mockito are being used for automated backend testing.

Current backend testing includes:

- Verifying that the backend starts successfully
- Checking that the health route works
- Creating game records
- Retrieving game records
- Updating game records
- Deleting game records
- Searching and filtering game records
- Testing pagination and sorting
- Registering users
- Logging in users
- Testing protected routes with JWT tokens
- Adding games to a user's library
- Updating a game's library status
- Removing games from a user's library
- Creating game reviews
- Updating game reviews
- Deleting game reviews
- Retrieving reviews for a game
- Retrieving reviews for the logged-in user
- Confirming average game ratings update after review changes
- Testing recommendation results
- Confirming validation behavior
- Confirming error handling behavior

Automated backend testing includes:

- Unit tests using JUnit
- Service tests using Mockito
- Controller tests for API endpoints
- Tests for successful service behavior
- Tests for exception and failure cases
- Tests for authentication-related logic
- Tests for game service logic
- Tests for library service logic
- Tests for review service logic
- Tests for recommendation service logic

Current frontend testing includes:

- Registering a new user from the frontend
- Logging in from the frontend
- Saving the JWT token after login
- Keeping the user authenticated after login
- Redirecting unauthenticated users away from protected pages
- Loading authenticated user profile data
- Sending authenticated requests with the JWT token
- Displaying backend data in frontend pages
- Testing loading and error states
- Confirming Tailwind CSS styling works across the application
- Testing that the application is functional through the browser

---

## What I Learned in Week 1

During Week 1, I practiced:

- Creating a Spring Boot project
- Running a Spring Boot application
- Building REST API routes
- Using Spring MVC annotations
- Using request bodies, path variables, and request parameters
- Separating code into controller, service, and repository layers
- Creating JPA entities
- Creating repositories with Spring Data JPA
- Creating request and response DTOs
- Validating incoming request data
- Returning proper HTTP responses
- Handling basic errors
- Testing endpoints in Postman

---

## What I Learned in Week 2

During Week 2, I practiced:

- Building more advanced GET endpoints
- Using request parameters for filtering
- Adding pagination with Spring Data
- Adding sorting to API results
- Filtering games by title, genre, platform, and release date
- Improving validation messages
- Improving error response formatting
- Adding sample data for testing
- Testing search, filtering, pagination, and sorting in Postman

---

## What I Learned in Week 3

During Week 3, I practiced:

- Adding Spring Security to a Spring Boot project
- Creating user registration and login functionality
- Hashing passwords before saving users
- Generating JWT tokens after login
- Validating JWT tokens on protected requests
- Creating a custom JWT authentication filter
- Using the authenticated user's email to access user-specific data
- Protecting API endpoints from unauthenticated users
- Creating user-owned resources
- Building a personal game library feature
- Updating and deleting records that belong to the authenticated user
- Handling authentication and library-related exceptions

---

## What I Learned in Week 4

During Week 4, I practiced:

- Creating review-related API endpoints
- Designing nested REST routes such as `/api/games/{gameId}/reviews`
- Separating review logic into a dedicated review controller and service
- Protecting review creation, update, and delete routes
- Making sure users can only update and delete their own reviews
- Checking whether a game exists before creating a review
- Checking whether a game is in the user's library before creating a review
- Preventing duplicate reviews from the same user for the same game
- Using pagination and sorting for review results
- Updating a game's average rating after review changes
- Using `@Transactional` for service methods that perform database changes
- Building the first version of a recommendation service
- Testing authenticated review and recommendation requests in Postman

---

## What I Learned in Week 5

During Week 5, I practiced:

- Creating a frontend application for a Spring Boot backend
- Organizing frontend code into pages, components, context, and route protection
- Using React Router for navigation
- Creating an Auth Context to manage login state across the application
- Creating protected routes for pages that require authentication
- Storing JWT tokens on the frontend
- Sending JWT tokens in the `Authorization` header for protected backend requests
- Fetching authenticated user data from the backend
- Using `useEffect` for API calls that should run when a page loads
- Managing loading and error states in React
- Connecting frontend forms to backend endpoints
- Using Tailwind CSS utility classes to style the application
- Improving the UI so the project looks more modern and portfolio-ready
- Testing the full application flow through the browser

---

## What I Learned in Week 6

During Week 6, I practiced:

- Writing unit tests with JUnit
- Using Mockito to mock dependencies
- Testing service-layer business logic
- Testing controller endpoints
- Using mock repositories and mock services
- Testing successful cases and failure cases
- Testing validation and exception behavior
- Testing authenticated service methods
- Understanding the difference between unit tests and manual Postman testing
- Improving backend reliability through automated tests
- Adding Swagger/OpenAPI documentation to a Spring Boot project
- Using Swagger UI to view and test documented API endpoints
- Adding Docker support to make the project easier to run
- Improving the project so it looks more professional and portfolio-ready

---

## API Features

Current API features include:

```text
Auth:
POST   /api/auth/register
POST   /api/auth/login
```

```text
Users:
GET    /api/users/me
GET    /api/users/me/reviews
```

```text
Games:
GET    /api/games
GET    /api/games/{id}
POST   /api/games
PUT    /api/games/{id}
DELETE /api/games/{id}
```

```text
Library:
GET    /api/library
POST   /api/library/{gameId}
PATCH  /api/library/{id}/status
DELETE /api/library/{id}
```

```text
Reviews:
POST   /api/games/{gameId}/reviews
GET    /api/games/{gameId}/reviews
PATCH  /api/reviews/{reviewId}
DELETE /api/reviews/{reviewId}
```

```text
Recommendations:
GET    /api/recommendations
```

```text
API Documentation:
GET    /swagger-ui/index.html
```

---

## Frontend Pages

Current frontend pages and flows include:

- Home or landing page
- Game browsing page
- Login page
- Register page
- Profile page
- Protected user pages
- Library-related user flow
- Recommendation-related user flow

---

## Future Features

Planned future features include:

- Complete deployment setup for the full-stack application
- Add production environment configuration
- Improve Docker setup for full-stack deployment
- Add integration testing
- Build admin frontend pages
- Add frontend create, update, and delete game forms for admin users
- Improve recommendation algorithm
- Add weighted recommendation scoring
- Add review moderation for admin users
- Add trending games
- Add top-rated games
- Add frontend form validation improvements
- Improve responsive design for smaller screens
- Add better success and error notifications

---

## Project Status

This project is currently in active development. The backend foundation, frontend foundation, authentication flow, protected routes, review system, recommendation system, automated testing, API documentation, and Docker support are now working. The project is now moving into the deployment phase.

Current milestones:

```text
Week 1 complete: Core Spring Boot backend foundation is working.
```

```text
Week 2 complete: The games API supports filtering, pagination, and sorting.
```

```text
Week 3 complete: User authentication, JWT security, protected routes, and the personal game library feature are working.
```

```text
Week 4 complete: Game reviews, average ratings, and the first version of personalized recommendations are working.
```

```text
Week 5 complete: The React frontend is connected to the backend, protected routes are working, Tailwind CSS is integrated, and the application has a modern functional UI.
```

```text
Week 6 complete: Unit tests, controller tests, Swagger/OpenAPI documentation, and Docker support have been added to improve project quality and maintainability.
```

Next milestone:

```text
Week 7 in progress: Prepare the full-stack application for deployment by configuring production settings, environment variables, Docker, hosting, and production-ready frontend/backend communication.
```
