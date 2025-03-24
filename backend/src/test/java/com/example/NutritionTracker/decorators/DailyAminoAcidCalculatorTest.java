package com.example.NutritionTracker.decorators;

import com.example.NutritionTracker.decorator.DailyAminoAcidCalculator;
import com.example.NutritionTracker.dto.UserDTO;
import com.example.NutritionTracker.entity.AminoAcidRequirement;
import com.example.NutritionTracker.repo.AminoAcidRequirementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyAminoAcidCalculatorTest {

    @Mock
    private AminoAcidRequirementRepository aminoAcidRequirementRepository; // Mock for the database

    @InjectMocks
    private DailyAminoAcidCalculator dailyAminoAcidCalculator; // Service under test

    private UserDTO testUser;

    @BeforeEach
    void setUp() {
        // Test user with a weight of 70 kg
        testUser = UserDTO.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .weight(70.0)
                .build();
    }

    @Test
    void testCalculateDailyNeeds_RepositoryReturnsValues() {
        // Simulated base values from the database (similar to DevDataLoader)
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
                new AminoAcidRequirement(null, "Glycin", 35.0) // Increased for anti-aging purposes
        );

        // Mock: Repository returns base values
        when(aminoAcidRequirementRepository.findAll()).thenReturn(mockRequirements);

        // Perform calculation
        Map<String, Double> dailyNeeds = dailyAminoAcidCalculator.calculateDailyNeeds(testUser);

        // Ensure that values are calculated
        assertNotNull(dailyNeeds, "The returned map must not be null");
        assertFalse(dailyNeeds.isEmpty(), "The map must not be empty. It should contain amino acids.");

        // Validate values for all amino acids
        assertEquals(2.1, dailyNeeds.get("Lysin"), 0.01);
        assertEquals(2.73, dailyNeeds.get("Leucin"), 0.01);
        assertEquals(1.4, dailyNeeds.get("Isoleucin"), 0.01);
        assertEquals(1.82, dailyNeeds.get("Valin"), 0.01);
        assertEquals(1.05, dailyNeeds.get("Methionin"), 0.01);
        assertEquals(1.75, dailyNeeds.get("Phenylalanin"), 0.01);
        assertEquals(1.05, dailyNeeds.get("Threonin"), 0.01);
        assertEquals(0.28, dailyNeeds.get("Tryptophan"), 0.01);
        assertEquals(0.7, dailyNeeds.get("Histidin"), 0.01);
        assertEquals(2.45, dailyNeeds.get("Glycin"), 0.01);
    }
}