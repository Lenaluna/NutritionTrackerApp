package com.example.NutritionTracker.decorators;

import com.example.NutritionTracker.decorator.DailyAminoAcidCalculator;
import com.example.NutritionTracker.decorator.LongevityAminoAcidDecorator;
import com.example.NutritionTracker.dto.UserDTO;
import com.example.NutritionTracker.entity.AminoAcidRequirement;
import com.example.NutritionTracker.repo.AminoAcidRequirementRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link LongevityAminoAcidDecorator}.
 * Ensures that amino acid requirements for longevity are correctly adjusted.
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class LongevityAminoAcidDecoratorTest {

    @Mock
    private AminoAcidRequirementRepository aminoAcidRequirementRepository;

    @InjectMocks
    private DailyAminoAcidCalculator dailyAminoAcidCalculator;

    private UserDTO testUser;

    @BeforeEach
    void setUp() {
        testUser = UserDTO.builder()
                .id(UUID.randomUUID())
                .name("Test Longevity User")
                .weight(70.0) // 70kg user
                .build();
    }

    @Test
    void testLongevityDecorator_CorrectAdjustments() {
        // **1. Mock database values (amino acid requirement per kg)**
        List<AminoAcidRequirement> mockRequirements = List.of(
                new AminoAcidRequirement(null, "Lysin", 30.0),
                new AminoAcidRequirement(null, "Leucin", 39.0),
                new AminoAcidRequirement(null, "Isoleucin", 20.0),
                new AminoAcidRequirement(null, "Valin", 26.0),
                new AminoAcidRequirement(null, "Methionin", 15.0),
                new AminoAcidRequirement(null, "Phenylalanin", 25.0),
                new AminoAcidRequirement(null, "Threonin", 15.0),
                new AminoAcidRequirement(null, "Tryptophan", 4.0),
                new AminoAcidRequirement(null, "Histidin", 10.0),
                new AminoAcidRequirement(null, "Glycin", 35.0)
        );

        when(aminoAcidRequirementRepository.findAll()).thenReturn(mockRequirements);

        // **2. Calculate base daily needs**
        Map<String, Double> baseNeeds = dailyAminoAcidCalculator.calculateDailyNeeds(testUser);
        log.info("Base Needs BEFORE Decorator: {}", baseNeeds);

        // **3. Apply Longevity Decorator**
        LongevityAminoAcidDecorator longevityDecorator = new LongevityAminoAcidDecorator(dailyAminoAcidCalculator);
        Map<String, Double> adjustedNeeds = longevityDecorator.calculateAminoAcids(baseNeeds);
        log.info("Adjusted Needs AFTER Decorator: {}", adjustedNeeds);

        // **4. Calculate expected values**
        Map<String, Double> expectedValues = new HashMap<>();
        List<String> reduceBy10 = List.of("Leucin", "Isoleucin", "Valin");
        List<String> increaseBy20 = List.of("Phenylalanin");
        List<String> increaseBy25 = List.of("Glycin");

        for (String aminoAcid : baseNeeds.keySet()) {
            double expectedValue = baseNeeds.get(aminoAcid); // Standard value

            if (aminoAcid.equals("Methionin")) expectedValue *= 0.8; // -20%
            if (aminoAcid.equals("Threonin")) expectedValue *= 0.95; // -5%
            if (aminoAcid.equals("Tryptophan")) expectedValue *= 1.1; // +10%
            if (aminoAcid.equals("Lysin")) expectedValue *= 1.05; // +5%
            if (reduceBy10.contains(aminoAcid)) expectedValue *= 0.9; // -10%
            if (increaseBy20.contains(aminoAcid)) expectedValue *= 1.2; // +20%
            if (increaseBy25.contains(aminoAcid)) expectedValue *= 1.25; // +25%

            expectedValues.put(aminoAcid, round(expectedValue));
        }

        // **5. Error checking**
        List<String> errorList = new ArrayList<>();
        for (String aminoAcid : expectedValues.keySet()) {
            checkValue(aminoAcid, expectedValues.get(aminoAcid), adjustedNeeds.get(aminoAcid), errorList);
        }

        // **6. If errors exist, fail the test**
        if (!errorList.isEmpty()) {
            String errorMessage = "Test failed due to incorrect values:\n" + String.join("\n", errorList);
            fail(errorMessage);
        }
    }

    /**
     * Checks if the calculated value matches the expected value.
     *
     * @param name       Name of the amino acid.
     * @param expected   Expected value.
     * @param actual     Actual value.
     * @param errorList  Error list if values do not match.
     */
    private void checkValue(String name, double expected, double actual, List<String> errorList) {
        double roundedExpected = round(expected);
        double roundedActual = round(actual);

        if (Math.abs(roundedExpected - roundedActual) > 0.01) {
            errorList.add(name + " - Expected: " + roundedExpected + ", Actual: " + roundedActual);
        }
    }

    /**
     * Rounds a value to 2 decimal places.
     *
     * @param value The value to be rounded.
     * @return Rounded value.
     */
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}