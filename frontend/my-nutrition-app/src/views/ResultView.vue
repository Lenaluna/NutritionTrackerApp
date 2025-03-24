<template>
  <v-container class="py-5">
    <h1 v-if="userName" data-cy="results-heading">Hallo {{ userName }}, hier sind deine Ergebnisse!</h1>
    <h1 v-else data-cy="results-heading">Hier sind deine Ergebnisse.</h1>

    <div v-if="profile">
      <h2 data-cy="selected-foods-heading">Ausgewählte Lebensmittel</h2>
      <v-list v-if="selectedFoods.length">
        <v-list-item v-for="food in selectedFoods" :key="food.id">
          <v-list-item-title>{{ food.name }}</v-list-item-title>
        </v-list-item>
      </v-list>
      <p v-else class="error">Keine Lebensmittel ausgewählt</p>

      <h2>Gesamt-Aminosäuren</h2>
      <v-table v-if="profile.aminoAcids" data-cy="amino-acid-table">
        <thead>
        <tr><th>Aminosäure</th><th>Menge (g)</th></tr>
        </thead>
        <tbody>
        <tr v-for="key in Object.keys(profile.dailyNeeds)" :key="key">
          <td>{{ key }}</td>
          <td>{{ profile.aminoAcids[key]?.toFixed(2) || "0.00" }}</td>
        </tr>
        </tbody>
      </v-table>
      <p v-else class="error">Keine Aminosäuredaten verfügbar</p>

      <h2>Tagesbedarf</h2>
      <v-table v-if="profile.dailyNeeds">
        <thead>
        <tr><th>Aminosäure</th><th>Benötigte Menge (g)</th></tr>
        </thead>
        <tbody>
        <tr v-for="(value, key) in profile.dailyNeeds" :key="key">
          <td>{{ key }}</td>
          <td>{{ value.toFixed(2) }}</td>
        </tr>
        </tbody>
      </v-table>
      <p v-else class="error">Keine Tagesbedarfsdaten verfügbar</p>

      <h2>Prozentuale Deckung</h2>
      <p v-if="profile.coverageMessage" class="coverage-message">{{ profile.coverageMessage }}</p>
      <v-table v-if="profile.dailyCoverage">
        <thead>
        <tr><th>Aminosäure</th><th>Deckung (%)</th></tr>
        </thead>
        <tbody>
        <tr v-for="(value, key) in profile.dailyCoverage" :key="key">
          <td>{{ key }}</td>
          <td>{{ value.toFixed(1) }}%</td>
        </tr>
        </tbody>
      </v-table>
      <p v-else class="error">Keine Deckungsdaten verfügbar</p>
    </div>

    <v-btn class="mt-4" @click="closeApp" data-cy="close-button">Zum Start</v-btn>
  </v-container>
</template>

<script>
import { ref, onMounted } from "vue";
import api from "../api";

export default {
  name: "ResultView",
  setup() {
    const profile = ref(null);
    const selectedFoods = ref([]);
    const userData = ref(null);
    const userName = ref("");
    const errorMessage = ref("");

    // Load user data and fetch API results
    onMounted(async () => {
      console.log("🔍 Lade Daten...");

      // Load user data from localStorage or API
      let storedUserData = JSON.parse(localStorage.getItem("userData"));
      if (!storedUserData) {
        try {
          console.log("📡 Fetching user data from API...");
          const response = await api.get("/user-data");
          storedUserData = response.data;
          localStorage.setItem("userData", JSON.stringify(storedUserData));
          console.log(" User data stored:", storedUserData);
        } catch (error) {
          console.error(" Failed to fetch user data:", error);
          errorMessage.value = "Fehler beim Abrufen der Benutzerdaten.";
          return;
        }
      }

      userData.value = storedUserData;
      userName.value = storedUserData?.name || "Unbekannter Benutzer";

      // Load selected foods from localStorage
      const storedSelectedFoods = localStorage.getItem("selectedFoods");
      if (storedSelectedFoods) {
        selectedFoods.value = JSON.parse(storedSelectedFoods);
      }

      try {
        // Fetch all required data in parallel
        const [aminoAcidsRes, dailyNeedsRes, coverageRes] = await Promise.all([
          api.post("/amino-profile/sum"),
          api.get("/amino-profile/daily-needs"),
          api.post("/amino-profile/coverage"),
        ]);

        const aminoAcids = aminoAcidsRes.data;
        const dailyNeeds = dailyNeedsRes.data;
        const dailyCoverage = coverageRes.data;

        // Generate coverage message
        const overallCoverage = Object.values(dailyCoverage).reduce((a, b) => a + b, 0) / Object.keys(dailyCoverage).length;
        const coverageMessage = `${userName.value}, deine durchschnittliche Aminosäurenabdeckung beträgt ${overallCoverage.toFixed(1)}%.`;

        profile.value = { aminoAcids, dailyNeeds, dailyCoverage, coverageMessage };
      } catch (error) {
        console.error(" Fehler beim Abrufen der Daten:", error);
        errorMessage.value = "Daten konnten nicht geladen werden.";
      }
    });

    // Function to close the application and clear storage
    const closeApp = () => {
      localStorage.clear();
      window.location.href = "/";
    };

    return { profile, selectedFoods, userData, userName, errorMessage, closeApp };
  },
};
</script>

<style scoped>
.v-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

.v-table th,
.v-table td {
  padding: 8px;
  border: 1px solid #ddd;
  text-align: left;
}

.v-table th {
  background-color: #f4f4f4;
}

.error {
  color: red;
  font-style: italic;
  font-weight: bold;
}

.coverage-message {
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 10px;
}
</style>