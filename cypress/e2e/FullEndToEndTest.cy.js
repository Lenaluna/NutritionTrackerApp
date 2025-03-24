describe("Full End-to-End Test for Nutrition Tracker", () => {
  beforeEach(() => {
    cy.clearLocalStorage();
    cy.visit("http://localhost:5173/");

    // Create test user via API before each test
    cy.request({
      method: "PUT",
      url: "http://localhost:8080/api/user-data/update",
      body: {
        id: "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        name: "Test User",
        weight: 70,
        age: 25,
        isAthlete: true,
        isVegan: true,
        isLongevityFocused: true,
      },
      failOnStatusCode: false // Avoid test failure if backend returns 500 (e.g., DB reset)
    }).then((response) => {
      if (response.status === 200) {
        localStorage.setItem("userData", JSON.stringify(response.body));
        cy.log("Test user stored:", JSON.stringify(response.body));
      } else {
        cy.log("Backend returned status " + response.status);
      }
    });
  });

  it("User navigates to food selection", () => {
    cy.visit("http://localhost:5173/selection");
    cy.get('[data-cy^="food-item-"]', { timeout: 10000 }).should("exist");
  });

  it("User selects foods and calculates amino acid profile", () => {
    cy.visit("http://localhost:5173/selection");

    // Validate local user data exists
    cy.wrap(null).then(() => {
      const userData = JSON.parse(localStorage.getItem("userData"));
      expect(userData).to.not.be.null;
      expect(userData.id).to.exist;
      cy.log("User data loaded from localStorage:", userData);
    });

    // Select three food items
    cy.get('[data-cy^="food-item-"]').each(($el, index) => {
      if (index < 3) {
        cy.wrap($el).click();
      }
    });

    // Intercept backend API calls
    cy.intercept("POST", "/api/amino-profile/sum").as("aminoProfileSum");
    cy.intercept("GET", "/api/amino-profile/daily-needs").as("dailyNeeds");
    cy.intercept("POST", "/api/amino-profile/coverage").as("coverage");

    // Ensure the calculate button is visible before clicking
    cy.get('[data-cy="calculate-button"]', { timeout: 10000 })
      .should("be.visible")
      .click();

    // Wait for backend responses and verify 200 OK
    cy.wait("@aminoProfileSum").its("response.statusCode").should("eq", 200);
    cy.wait("@dailyNeeds").its("response.statusCode").should("eq", 200);
    cy.wait("@coverage").its("response.statusCode").should("eq", 200);

    // Check for navigation to the result view
    cy.url({ timeout: 10000 }).should("include", "/results");
    cy.get('[data-cy="results-heading"]', { timeout: 10000 }).should("be.visible");
  });

  it("Displays correct results and allows user to restart", () => {
    cy.visit("http://localhost:5173/results");

    // Confirm the user's name is displayed in the greeting
    cy.get('[data-cy="results-heading"]', { timeout: 10000 })
      .should("contain", "Hallo Test User");

    // Verify food list and amino acid table are shown
    cy.get('[data-cy="selected-foods-heading"]').should("exist");
    cy.get('[data-cy="amino-acid-table"]').should("exist");

    // Click button to return to home page
    cy.get('[data-cy="close-button"]').click();
    cy.url().should("eq", "http://localhost:5173/");
  });
});
