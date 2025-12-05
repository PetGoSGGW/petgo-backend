# PetGo Backend

Backend service for the PetGo application. Built with Spring Boot and Hibernate.

## Database

For the development profile, a local PostgreSQL database is required.
The database is not created automatically, so you must create it manually before running the backend.

The production database is not defined yet and will be configured later once deployment details are finalized.

For development, create a local database (example name: petgodb) and set the required connection values through environment variables defined in application-dev.properties
