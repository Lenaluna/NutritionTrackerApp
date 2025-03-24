package com.example.NutritionTracker.decorator;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code VeganAminoAcidDecorator} adjusts amino acid requirements to account for common deficiencies
 * in plant-based diets. It increases the intake of key amino acids that are often limited in vegan protein sources.
 * The adjustments are based on nutritional needs for individuals following a plant-based diet.
 */
public class VeganAminoAcidDecorator extends AminoAcidCalculatorDecorator {

    /**
     * Constructs a {@code VeganAminoAcidDecorator} that wraps an existing {@link AminoAcidCalculator}.
     *
     * @param wrapped The base calculator that this decorator modifies.
     */
    public VeganAminoAcidDecorator(AminoAcidCalculator wrapped) {
        super(wrapped);
    }

    /**
     * Modifies the base amino acid needs by increasing essential amino acids that are often lacking in a vegan diet.
     *
     * @param dailyNeeds The base daily amino acid requirements.
     * @return A modified map of amino acid requirements with adjustments for a vegan diet.
     */
    @Override
    public Map<String, Double> calculateAminoAcids(Map<String, Double> dailyNeeds) {
        // Create a copy of the calculated values to avoid side effects
        Map<String, Double> aminoAcids = new HashMap<>(super.calculateAminoAcids(dailyNeeds));

        // Adjustments for essential amino acids in a vegan diet
        aminoAcids.put("Lysin", round(aminoAcids.getOrDefault("Lysin", 0.0) * 1.25));
        aminoAcids.put("Methionin", round(aminoAcids.getOrDefault("Methionin", 0.0) * 1.2));
        aminoAcids.put("Tryptophan", round(aminoAcids.getOrDefault("Tryptophan", 0.0) * 1.15));
        aminoAcids.put("Threonin", round(aminoAcids.getOrDefault("Threonin", 0.0) * 1.15));
        aminoAcids.put("Leucin", round(aminoAcids.getOrDefault("Leucin", 0.0) * 1.1));
        aminoAcids.put("Histidin", round(aminoAcids.getOrDefault("Histidin", 0.0) * 1.1));
        aminoAcids.put("Isoleucin", round(aminoAcids.getOrDefault("Isoleucin", 0.0) * 1.05));
        aminoAcids.put("Valin", round(aminoAcids.getOrDefault("Valin", 0.0) * 1.1));

        // Additional adjustments for plant-based diets
        aminoAcids.put("Phenylalanin", round(aminoAcids.getOrDefault("Phenylalanin", 0.0) * 1.2));
        aminoAcids.put("Glycin", round(aminoAcids.getOrDefault("Glycin", 0.0) * 1.2));

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