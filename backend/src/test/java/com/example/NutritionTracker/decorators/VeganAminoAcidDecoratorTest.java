package com.example.NutritionTracker.decorators;

import com.example.NutritionTracker.decorator.DailyAminoAcidCalculator;
import com.example.NutritionTracker.decorator.VeganAminoAcidDecorator;
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
 * Unit test for {@link VeganAminoAcidDecorator}.
 * Ensures that the amino acid requirements for a vegan diet are correctly adjusted.
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class VeganAminoAcidDecoratorTest {

    @Mock
    private AminoAcidRequirementRepository aminoAcidRequirementRepository;

    @InjectMocks
    private DailyAminoAcidCalculator dailyAminoAcidCalculator;

    private UserDTO testUser;

    /**
     * Initializes the test user before each test.
     */
    @BeforeEach
    void setUp() {
        testUser = UserDTO.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .weight(70.0) // Weight of the user
                .isVegan(true) // Vegan mode enabled
                .build();
    }

    /**
     * Tests whether the VeganAminoAcidDecorator correctly adjusts amino acid requirements.
     */
    @Test
    void testVeganDecorator_CorrectAdjustments() {
        // Simulated base values per kg body weight (as defined in DevDataLoader)
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

        // Base needs calculation (before decorator adjustments)
        Map<String, Double> baseNeeds = dailyAminoAcidCalculator.calculateDailyNeeds(testUser);
        log.info("Before adjustments: {}", baseNeeds);

        // Apply the Vegan Decorator
        VeganAminoAcidDecorator veganDecorator = new VeganAminoAcidDecorator(dailyAminoAcidCalculator);
        Map<String, Double> adjustedNeeds = veganDecorator.calculateAminoAcids(baseNeeds);
        log.info("After adjustments: {}", adjustedNeeds);

        // Expected values
        List<String> errorList = new ArrayList<>();
        checkValue("Lysin", baseNeeds.get("Lysin") * 1.25, adjustedNeeds.get("Lysin"), errorList);
        checkValue("Methionin", baseNeeds.get("Methionin") * 1.2, adjustedNeeds.get("Methionin"), errorList);
        checkValue("Tryptophan", baseNeeds.get("Tryptophan") * 1.15, adjustedNeeds.get("Tryptophan"), errorList);
        checkValue("Threonin", baseNeeds.get("Threonin") * 1.15, adjustedNeeds.get("Threonin"), errorList);
        checkValue("Leucin", baseNeeds.get("Leucin") * 1.1, adjustedNeeds.get("Leucin"), errorList);
        checkValue("Histidin", baseNeeds.get("Histidin") * 1.1, adjustedNeeds.get("Histidin"), errorList);
        checkValue("Isoleucin", baseNeeds.get("Isoleucin") * 1.05, adjustedNeeds.get("Isoleucin"), errorList);
        checkValue("Valin", baseNeeds.get("Valin") * 1.1, adjustedNeeds.get("Valin"), errorList);
        checkValue("Phenylalanin", baseNeeds.get("Phenylalanin") * 1.2, adjustedNeeds.get("Phenylalanin"), errorList);
        checkValue("Glycin", baseNeeds.get("Glycin") * 1.2, adjustedNeeds.get("Glycin"), errorList);

        // If errors exist, print them and fail the test
        if (!errorList.isEmpty()) {
            log.error("Errors found: {}", errorList);
            fail("Test failed due to incorrect values.");
        }
    }

    /**
     * Validates whether a calculated value is within the accepted margin of error.
     *
     * @param name      The name of the amino acid.
     * @param expected  The expected value after rounding.
     * @param actual    The actual value received.
     * @param errorList A list to collect errors if values are incorrect.
     */
    private void checkValue(String name, double expected, double actual, List<String> errorList) {
        double roundedExpected = Math.round(expected * 100.0) / 100.0;
        if (Math.abs(roundedExpected - actual) > 0.01) {
            errorList.add(name + " incorrect! Expected: " + roundedExpected + " | Actual: " + actual);
        }
    }
}

