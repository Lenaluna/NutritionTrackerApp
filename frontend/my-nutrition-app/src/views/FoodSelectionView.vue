<template>
  <v-container class="py-5">
    <h1 data-cy="food-selection-heading">Wähle deine Lebensmittel</h1>

    <v-list>
      <v-list-item
        v-for="food in foodItems"
        :key="food.id"
        @click="toggleFoodSelection(food)"
        :class="{ 'selected': selectedFoods.includes(food) }"
        :data-cy="`food-item-${food.id}`"
      >
        <v-list-item-title>{{ food.name }}</v-list-item-title>
      </v-list-item>
    </v-list>

    <v-btn
      class="mt-4"
      @click="processSelections"
      :disabled="!selectedFoods.length"
      data-cy="calculate-button"
    >
      Aminosäureprofil berechnen
    </v-btn>
  </v-container>
</template>

<script>
import { ref, onMounted } from "vue";
import api from "../api";

export default {
  name: "FoodSelectionView",
  setup() {
    const foodItems = ref([]); // All available food items
    const selectedFoods = ref([]); // Selected food items
    const nutritionLogId = ref(null); // Stores the created NutritionLog ID

    // Fetch food items from the API when the component is mounted
    onMounted(async () => {
      try {
        const res = await api.get("/food-items/all");
        foodItems.value = res.data;
      } catch (error) {
        console.error(" Error fetching food items:", error);
      }
    });

    // Ensure user exists before creating a NutritionLog
    const getUserData = async () => {
      let userData = JSON.parse(localStorage.getItem("userData"));

      if (!userData?.id) {
        try {
          console.log("📡 Fetching user data from API...");
          const response = await api.get("/user-data");
          userData = response.data;
          localStorage.setItem("userData", JSON.stringify(userData));
          console.log(" User data stored:", userData);
        } catch (error) {
          console.error(" Failed to fetch user data:", error);
          return null;
        }
      }
      return userData;
    };

    // Create a new NutritionLog
    const createNutritionLog = async () => {
      const userData = await getUserData();
      if (!userData?.id) return;

      try {
        const response = await api.post("/nutrition-logs/create", { userId: userData.id });
        nutritionLogId.value = response.data.id;
        localStorage.setItem("nutritionLogId", nutritionLogId.value);
        console.log(" NutritionLog created with ID:", nutritionLogId.value);
      } catch (error) {
        console.error(" Failed to create NutritionLog:", error);
      }
    };

    // Select or deselect a food item
    const toggleFoodSelection = (food) => {
      const index = selectedFoods.value.findIndex((item) => item.id === food.id);
      index === -1 ? selectedFoods.value.push(food) : selectedFoods.value.splice(index, 1);
    };

    // Process selections: Create NutritionLog and add selected food items
    const processSelections = async () => {
      await createNutritionLog();
      if (!nutritionLogId.value) return;

      try {
        // Add selected food items in parallel
        await Promise.all(
          selectedFoods.value.map((food) =>
            api.post(`/nutrition-logs/${nutritionLogId.value}/food-items/${food.id}`)
          )
        );

        console.log(" All selected foods added to NutritionLog.");
        localStorage.setItem("selectedFoods", JSON.stringify(selectedFoods.value));
        window.location.href = "/results"; // Navigate to results page
      } catch (error) {
        console.error(" Error adding food items:", error);
      }
    };

    return { foodItems, selectedFoods, toggleFoodSelection, processSelections };
  },
};
</script>

<style scoped>
.selected {
  background-color: lightblue !important;
  color: black !important;
  font-weight: bold;
  border-radius: 5px;
}
</style>