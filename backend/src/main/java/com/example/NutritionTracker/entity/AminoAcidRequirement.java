package com.example.NutritionTracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entity representing the daily amino acid requirement per kilogram of body weight.
 * This class defines the base nutritional needs for essential amino acids.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AminoAcidRequirement {

    /** The unique identifier for the amino acid requirement entry. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** The name of the amino acid (must be unique). */
    @Column(nullable = false, unique = true)
    private String aminoAcid;

    /** The base required amount of this amino acid per kilogram of body weight. */
    @Column(nullable = false)
    private double baseAmountPerKg;

    /**
     * Constructor for creating an AminoAcidRequirement without specifying an ID.
     * This is useful for testing and when manually creating entries before persistence.
     *
     * @param aminoAcid The name of the amino acid.
     * @param baseAmountPerKg The required amount per kg body weight.
     */
    public AminoAcidRequirement(String aminoAcid, double baseAmountPerKg) {
        this.aminoAcid = aminoAcid;
        this.baseAmountPerKg = baseAmountPerKg;
    }
}