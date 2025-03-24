# Nutrition Tracker

A **complete tracker for daily nutrition** that analyzes consumed foods and calculates amino acid requirements.

## Features

- **Food Selection:** Users can select the foods they have consumed.
- **Amino Acid Analysis:** Calculates the amino acids contained in selected foods.
- **Daily Requirement Comparison:** Compares consumed profile against recommended requirements.
- **User Profiles:** Customizes calculations based on individual parameters (Athlete, Vegan, Longevity).
- **API Documentation:** Automatically generated using **OpenAPI**.
- **End-to-End Testing:** Implemented with **Cypress** for full UI coverage.

## Technologies & Requirements

### Backend

- **Tech Stack:**
  - [Spring Boot](https://spring.io/projects/spring-boot) 2.5.4
  - [H2 Database](https://www.h2database.com/) 1.4.200
  - Maven 3.6+
  - Java 17

### Frontend

- **Tech Stack:**
  - [Vue.js](https://vuejs.org/) 3.2.0
  - [Vuetify](https://vuetifyjs.com/) 2.5.0
  - [Cypress](https://www.cypress.io/) 8.3.0
  - Node.js 14+
  - npm 6+

### Starting the Application (Backend & Frontend simultaneously)

If your terminal is already in the root directory of the application (`NutritionTrackerApp`), first navigate to the frontend folder:

```sh
cd frontend/my-nutrition-app/
```

Then start the application with the following command:

```sh
npm start
```

 1.	Start both the backend and frontend as described above.
 2.	Open http://localhost:5173 in your browser.
 3.	Enter your user data and follow the instructions for food selection and results display.
---


### Project Structure

```sh
├── backend/       # Spring Boot backend  
│   ├── src/       # Backend source code  
│   ├── pom.xml    # Maven configuration  
├── frontend/      # Vue 3 frontend with Vite  
│   ├── src/       # Frontend source code  
│   ├── package.json  # npm configuration file  
```

---

### API Documentation
API Documentation

The OpenAPI specification (openapi.yaml) describes all available endpoints.
Accessible at:
[`http://localhost:8080/api/swagger-ui/index.html#/`](http://localhost:8080/api/swagger-ui/index.html#/)

### Tests
- **Unit-Tests:** Cover the Decorator logic and the summation of amino acid profiles.

- **End-to-End Tests:**
  To run the end-to-end tests with Cypress, follow these steps:

1. Make sure your terminal is in the root directory of the application (`NutritionTrackerApp`).
2. Navigate to the frontend folder:

   ```sh
   cd frontend/my-nutrition-app/
   ```

3. Start Cypress:

   ```sh
   npx cypress open
   ```

---

### License
This project is licensed under the MIT License. For Details, see [`LICENSE`](LICENSE).