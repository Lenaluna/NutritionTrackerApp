package com.example.NutritionTracker.decorator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Decorator class for adjusting amino acid requirements for athletes.
 * Increases Branched-Chain Amino Acids (BCAAs) and other essential amino acids
 * to reflect higher protein needs in physically active individuals.
 */
public class AthleteAminoAcidDecorator extends AminoAcidCalculatorDecorator {

    /**
     * Constructs an {@code AthleteAminoAcidDecorator} with the given base calculator.
     *
     * @param wrapped The base {@code AminoAcidCalculator} instance to be decorated.
     */
    public AthleteAminoAcidDecorator(AminoAcidCalculator wrapped) {
        super(wrapped);
    }

    /**
     * Adjusts the daily amino acid requirements for athletes.
     * Increases BCAAs (Leucine, Isoleucine, Valine) by 30% and all other amino acids by 15%.
     *
     * @param dailyNeeds A map of baseline amino acid requirements before adjustments.
     * @return A modified map with increased amino acid needs for athletes.
     */
    @Override
    public Map<String, Double> calculateAminoAcids(Map<String, Double> dailyNeeds) {
        // Create a copy of the calculated values to avoid side effects
        Map<String, Double> aminoAcids = new HashMap<>(super.calculateAminoAcids(dailyNeeds));

        // List of BCAAs
        List<String> BCAAs = List.of("Leucin", "Isoleucin", "Valin");

        // Increase BCAAs by 30%
        for (String bcaa : BCAAs) {
            aminoAcids.computeIfPresent(bcaa, (k, v) -> v * 1.3);
        }

        // Increase all other amino acids by 15%, except for BCAAs
        aminoAcids.replaceAll((key, value) -> BCAAs.contains(key) ? value : value * 1.15);

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