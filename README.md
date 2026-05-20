# NextPlay Backend

NextPlay is a Spring Boot backend for a game discovery and recommendation platform. The goal of this project is to help users discover games, manage game data, and eventually receive personalized recommendations based on ratings, reviews, genres, platforms, and user activity.

This project is being built step-by-step while relearning and practicing Spring Boot.

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

---

## Project Goal

The final goal of NextPlay is to become a full game discovery platform where users can:

- Browse games
- Search and filter games
- View game details
- Create accounts
- Save games to a personal library
- Mark games as want to play, playing, completed, or dropped
- Rate and review games
- Receive personalized recommendations
- View trending and top-rated games

Admin users will eventually be able to:

- Add games
- Update games
- Delete games
- Manage game information
- Moderate reviews

---

## Tech Stack

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Bean Validation
- Maven
- MySQL or PostgreSQL
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
- Searching game records
- Confirming validation behavior
- Confirming basic error handling

---

## What I Learned In Week 1

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

## Week 2 Focus

Week 2 will focus on improving the game API and making it more professional.

Planned Week 2 work:

- Improve relationships between games, genres, and platforms
- Improve filtering by title, genre, and platform
- Add pagination to game results
- Add sorting to game results
- Improve validation messages
- Improve error response formatting
- Add more sample game data
- Continue testing with Postman
- Update the README with Week 2 progress

---

## Future Features

Planned future features include:

- User registration
- User login
- JWT authentication
- Role-based authorization
- User game library
- Game status tracking
- Reviews and ratings
- Recommendation engine
- Trending games
- Top-rated games
- Swagger/OpenAPI documentation
- Unit and integration testing
- Docker support
- Frontend connection with React and TypeScript

---

## Project Status

This project is currently in early development.

Current milestone:

```text
Week 1 complete: Core Spring Boot backend foundation is working.
```

Next milestone:

```text
Week 2: Make the Game API clean, searchable, filterable, paginated, and professional.
```
