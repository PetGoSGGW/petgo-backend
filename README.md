# PetGo Backend

Backend service for the PetGo application. Built with Spring Boot and Hibernate.

## Database

For the development profile, a local PostgreSQL database is required.
The database is not created automatically, so you must create it manually before running the backend.

The production database is not defined yet and will be configured later once deployment details are finalized.

For development, create a local database (example name: petgodb) and set the required connection values through environment variables defined in application-dev.properties.

## Branching Strategy
Our branching strategy consists of the following:
- `main` - The production branch. Changes here have been reviewed, tested locally, merged into the test environment, and validated there.
- `test` - The development branch. Changes that have been reviewed and tested locally are merged here for further testing in the test environment.
- `feature` - A branch dedicated to a specific feature. The branch name should include the key of the corresponding feature. It is recommended to create sub-branches for individual tasks, which will later be merged into the feature branch. There is no strict convention here, however `work` prefix is recommended.

### Example feature branch name
- `feature/PGB-01/Add-very-exciting-functionality`
### Example sub-branch for a dedicated task
- `work/PGB-01/Make-the-dog-bark`

## How to Start Development
1. Navigate to the test branch - This branch contains the latest changes: </br>
`git checkout test`
2. Update your local branch - Make sure you have the newest changes: </br>
`git pull`
3. Create a new feature branch - Branch out from test for your feature: </br>
`git checkout -b feature/PGB-01/Very-very-important-feature`
4. Create a sub-branch for a specific task - Branch out again from your feature branch: </br>
`git checkout -b work/PGB-01/Untangle-spaghetti-code-in-lasagna-component`
5. Make changes and commit - Add your changes and commit them as usual.
6. Push your sub-branch - Push your work branch to the remote repository: </br>
`git push`
7. Open a Pull Request (PR) - On GitHub, create a `work` -> `feature` PR and request a review.
8. Merge subtasks into the feature branch - Repeat the above steps for all subtasks of your feature.
9. Create a `feature` → `test` PR - Once all subtasks are complete and merged into the feature branch, open a PR from `feature` → `test` and request a review. Ensure that all changes have been tested locally before submitting.


