package com.example.NutritionTracker.service;

import com.example.NutritionTracker.decorator.*;
import com.example.NutritionTracker.dto.UserDTO;
import com.example.NutritionTracker.entity.FoodItem;
import com.example.NutritionTracker.entity.NutritionLog;
import com.example.NutritionTracker.entity.NutritionLogFoodItem;
import com.example.NutritionTracker.repo.NutritionLogRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service responsible for managing amino acid profile calculations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AminoProfileService {
    private final DailyAminoAcidCalculator dailyAminoAcidCalculator;
    private final NutritionLogRepository nutritionLogRepository;
    private final UserDataService userDataService;

    /**
     * Calculates the sum of all consumed amino acids in the latest nutrition log.
     * @return A map containing amino acids and their summed values.
     */
    @Transactional(readOnly = true)
    public Map<String, Double> calculateAminoAcidSumsForLatestLog() {
        log.info("Calculating amino acid sums for the latest nutrition log...");

        NutritionLog latestLog = nutritionLogRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new IllegalArgumentException("No NutritionLog found!"));

        log.info("Found NutritionLog with ID: {}", latestLog.getId());

        if (latestLog.getFoodItems() == null || latestLog.getFoodItems().isEmpty()) {
            log.warn("No food items found in the latest NutritionLog.");
            return Collections.emptyMap();
        }

        Map<String, Double> aminoAcidSums = new HashMap<>();
        for (NutritionLogFoodItem logFoodItem : latestLog.getFoodItems()) {
            FoodItem foodItem = logFoodItem.getFoodItem();
            log.info("Processing FoodItem: {}", foodItem.getName());

            if (foodItem.getAminoAcidProfile() != null) {
                foodItem.getAminoAcidProfile().forEach((amino, value) -> aminoAcidSums.merge(amino, value, Double::sum));
            }
        }

        log.info("Amino acid sums calculated: {}", aminoAcidSums);
        return aminoAcidSums;
    }

    /**
     * Calculates the daily amino acid requirements based on the user's weight and profile settings.
     * @return A map of amino acids with their required daily intake values.
     */
    @Transactional(readOnly = true)
    public Map<String, Double> calculateDailyAminoAcidNeeds() {
        UserDTO userDTO = userDataService.getUser()
                .orElseThrow(() -> new EntityNotFoundException("No user found"));

        Map<String, Double> dailyNeeds = dailyAminoAcidCalculator.calculateDailyNeeds(userDTO);
        log.info("Base daily amino acid needs for {}: {}", userDTO.getName(), dailyNeeds);

        AminoAcidCalculator calculator = dailyAminoAcidCalculator;

        if (Boolean.TRUE.equals(userDTO.getIsAthlete())) {
            calculator = new AthleteAminoAcidDecorator(calculator);
            log.info("Applying AthleteAminoAcidDecorator for user: {}", userDTO.getName());
        }

        if (Boolean.TRUE.equals(userDTO.getIsVegan())) {
            calculator = new VeganAminoAcidDecorator(calculator);
            log.info("Applying VeganAminoAcidDecorator for user: {}", userDTO.getName());
        }

        if (Boolean.TRUE.equals(userDTO.getIsLongevityFocused())) {
            calculator = new LongevityAminoAcidDecorator(calculator);
            log.info("Applying LongevityAminoAcidDecorator for user: {}", userDTO.getName());
        }

        Map<String, Double> adjustedNeeds = calculator.calculateAminoAcids(dailyNeeds);
        log.info("Final daily amino acid needs for {}: {}", userDTO.getName(), adjustedNeeds);
        return adjustedNeeds;
    }

    /**
     * Calculates the amino acid coverage percentage based on the latest log and daily requirements.
     * @return A map containing amino acids and their percentage coverage values.
     */
    @Transactional(readOnly = true)
    public Map<String, Double> calculateAminoAcidCoverageForLatestLog() {
        log.info("Calculating amino acid coverage for the latest NutritionLog...");

        Map<String, Double> dailyNeeds = calculateDailyAminoAcidNeeds();
        if (dailyNeeds == null || dailyNeeds.isEmpty()) {
            log.error("Error: No daily amino acid requirements found!");
            return Collections.emptyMap();
        }
        log.info("Daily amino acid requirements: {}", dailyNeeds);

        Map<String, Double> consumedAminoAcids = calculateAminoAcidSumsForLatestLog();
        if (consumedAminoAcids == null || consumedAminoAcids.isEmpty()) {
            log.warn("No consumed amino acids found. Returning empty result.");
            return Collections.emptyMap();
        }
        log.info("Consumed amino acids: {}", consumedAminoAcids);

        Map<String, Double> coverage = new HashMap<>();
        for (String aminoAcid : dailyNeeds.keySet()) {
            double need = dailyNeeds.getOrDefault(aminoAcid, 0.0);
            double consumed = consumedAminoAcids.getOrDefault(aminoAcid, 0.0);

            if (need > 0) {
                double percentage = Math.round((consumed / need) * 100 * 100.0) / 100.0;
                coverage.put(aminoAcid, percentage);
            } else {
                log.warn("Daily need for {} is 0. Preventing division by zero.", aminoAcid);
                coverage.put(aminoAcid, 0.0);
            }
        }

        log.info("Amino acid coverage calculated: {}", coverage);
        return coverage;
    }
}
