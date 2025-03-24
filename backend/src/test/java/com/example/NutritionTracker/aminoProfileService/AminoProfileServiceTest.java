package com.example.NutritionTracker.aminoProfileService;

import com.example.NutritionTracker.entity.FoodItem;
import com.example.NutritionTracker.entity.NutritionLog;
import com.example.NutritionTracker.entity.NutritionLogFoodItem;
import com.example.NutritionTracker.repo.NutritionLogRepository;
import com.example.NutritionTracker.service.AminoProfileService;
import com.example.NutritionTracker.service.UserDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AminoProfileServiceTest {

    @InjectMocks
    private AminoProfileService aminoProfileService;

    @Mock
    private UserDataService userDataService;

    @Mock
    private NutritionLogRepository nutritionLogRepository;

    @Test
    void testCalculateAminoAcidSumsForLatestLog() {
        // Create two sample amino acid profiles for two food items
        Map<String, Double> profile1 = Map.of(
                "Lysin", 1.0,
                "Leucin", 2.0,
                "Isoleucin", 1.5,
                "Valin", 1.2,
                "Methionin", 0.8,
                "Phenylalanin", 1.1,
                "Threonin", 1.3,
                "Tryptophan", 0.7,
                "Histidin", 0.9,
                "Glycin", 1.4
        );

        Map<String, Double> profile2 = Map.of(
                "Lysin", 0.6,
                "Leucin", 1.8,
                "Isoleucin", 1.0,
                "Valin", 0.9,
                "Methionin", 0.5,
                "Phenylalanin", 1.2,
                "Threonin", 0.8,
                "Tryptophan", 0.6,
                "Histidin", 1.0,
                "Glycin", 1.1
        );

        // Create mock food items with the above amino acid profiles
        FoodItem food1 = FoodItem.builder().name("Tofu").aminoAcidProfile(profile1).build();
        FoodItem food2 = FoodItem.builder().name("Beans").aminoAcidProfile(profile2).build();

        // Wrap the food items inside NutritionLogFoodItem instances
        NutritionLogFoodItem logItem1 = new NutritionLogFoodItem();
        logItem1.setFoodItem(food1);
        NutritionLogFoodItem logItem2 = new NutritionLogFoodItem();
        logItem2.setFoodItem(food2);

        // Create a mock NutritionLog containing the two food items
        NutritionLog log = NutritionLog.builder()
                .id(UUID.randomUUID())
                .foodItems(List.of(logItem1, logItem2))
                .build();

        // Mock repository behavior to return the created NutritionLog
        when(nutritionLogRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(log));

        // Call the method under test
        Map<String, Double> result = aminoProfileService.calculateAminoAcidSumsForLatestLog();

        // Assert that all amino acid sums are correct
        assertAll(
                () -> assertEquals(1.6, result.get("Lysin"), 0.0001),
                () -> assertEquals(3.8, result.get("Leucin"), 0.0001),
                () -> assertEquals(2.5, result.get("Isoleucin"), 0.0001),
                () -> assertEquals(2.1, result.get("Valin"), 0.0001),
                () -> assertEquals(1.3, result.get("Methionin"), 0.0001),
                () -> assertEquals(2.3, result.get("Phenylalanin"), 0.0001),
                () -> assertEquals(2.1, result.get("Threonin"), 0.0001),
                () -> assertEquals(1.3, result.get("Tryptophan"), 0.0001),
                () -> assertEquals(1.9, result.get("Histidin"), 0.0001),
                () -> assertEquals(2.5, result.get("Glycin"), 0.0001)
        );
    }
}