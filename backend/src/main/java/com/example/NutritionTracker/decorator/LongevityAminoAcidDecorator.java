package com.example.NutritionTracker.decorator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code LongevityAminoAcidDecorator} adjusts amino acid requirements to promote longevity.
 * This decorator applies specific increases and decreases to certain amino acids
 * based on research suggesting their impact on aging and overall health.
 */
public class LongevityAminoAcidDecorator extends AminoAcidCalculatorDecorator {

    /**
     * Constructs a {@code LongevityAminoAcidDecorator} that wraps an existing {@link AminoAcidCalculator}.
     *
     * @param wrapped The base calculator that this decorator modifies.
     */
    public LongevityAminoAcidDecorator(AminoAcidCalculator wrapped) {
        super(wrapped);
    }

    /**
     * Adjusts the daily amino acid requirements for longevity.
     * Increases Glycin and Phenylalanin while reducing Methionin, Leucin, and other aging-related amino acids.
     *
     * @param dailyNeeds A map of baseline amino acid requirements before adjustments.
     * @return A modified map with longevity-focused adjustments.
     */
    @Override
    public Map<String, Double> calculateAminoAcids(Map<String, Double> dailyNeeds) {
        // Create a copy of the calculated values to avoid side effects
        Map<String, Double> aminoAcids = new HashMap<>(super.calculateAminoAcids(dailyNeeds));

        // List of amino acids that will be increased or decreased**
        List<String> reduceBy10 = List.of("Leucin", "Isoleucin", "Valin");
        List<String> increaseBy20 = List.of("Phenylalanin");
        List<String> increaseBy25 = List.of("Glycin");

        // Reduce specific amino acids**
        aminoAcids.computeIfPresent("Methionin", (k, v) -> v * 0.8); // -20%
        aminoAcids.computeIfPresent("Threonin", (k, v) -> v * 0.95); // -5%
        aminoAcids.computeIfPresent("Tryptophan", (k, v) -> v * 1.1); // +10%
        aminoAcids.computeIfPresent("Lysin", (k, v) -> v * 1.05); // +5%

        // Increase or decrease grouped amino acids
        for (String aa : reduceBy10) {
            aminoAcids.computeIfPresent(aa, (k, v) -> v * 0.9); // -10%
        }
        for (String aa : increaseBy20) {
            aminoAcids.computeIfPresent(aa, (k, v) -> v * 1.2); // +20%
        }
        for (String aa : increaseBy25) {
            aminoAcids.computeIfPresent(aa, (k, v) -> v * 1.25); // +25%
        }

        // Round values to two decimal places
        aminoAcids.replaceAll((key, value) -> round(value));

        return aminoAcids;
    }

    /**
     * Helper method to round values to 2 decimal places.
     *
     * @param value The value to be rounded.
     * @return The rounded value with 2 decimal places.
     */
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
