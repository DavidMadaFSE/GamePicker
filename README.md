# NextPlay Backend

NextPlay is a Spring Boot backend for a game discovery and recommendation platform.

The goal of this project is to help users browse games, search and filter game data, save games to a personal library, track game progress, and eventually receive personalized recommendations based on ratings, reviews, genres, platforms, and user activity.

This project is being built step-by-step while relearning and practicing Spring Boot backend development.

---

## Project Goal

The final goal of NextPlay is to become a full backend platform where users can:

- Browse games
- Search and filter games
- View game details
- Create accounts
- Log in securely
- Save games to a personal library
- Track games as want to play, playing, completed, or dropped
- Rate and review games
- Receive personalized game recommendations
- View trending and top-rated games

Admin users will eventually be able to:

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

---

## Core Features Implemented

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
- Protected routes
- Personal user game library
- Game status tracking

---

## Tech Stack

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- Bean Validation
- Maven
- PostgreSQL or MySQL
- Postman

---

## How to Run the Project

### 1. Clone the repository

```bash
git clone <your-repository-url>
cd nextplay-backend
```

### 2. Configure the database

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

### 3. Run the application

```bash
mvn spring-boot:run
```

The backend should start on:

```text
http://localhost:8080
```

---

## Testing

Postman is being used to manually test the backend.

Current testing includes:

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
- Confirming validation behavior
- Confirming error handling behavior

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

## Future Features

Planned future features include:

- Reviews and ratings
- Recommendation engine
- Trending games
- Top-rated games
- Role-based authorization for admin users
- Swagger/OpenAPI documentation
- Unit and integration testing
- Docker support
- Frontend connection with React and TypeScript

---

## Project Status

This project is currently in early development, but the main backend foundation is working.

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

Next milestone:

```text
Week 4 in progress: Users should be able to write reviews for games, and the backend should begin supporting game recommendations.
```