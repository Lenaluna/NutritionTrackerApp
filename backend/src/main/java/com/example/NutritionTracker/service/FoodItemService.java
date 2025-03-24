package com.example.NutritionTracker.service;

import com.example.NutritionTracker.dto.FoodItemDTO;
import com.example.NutritionTracker.entity.FoodItem;
import com.example.NutritionTracker.repo.FoodItemRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing FoodItem entities.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;

    /**
     * Retrieves all available FoodItems as DTOs.
     *
     * @return List of FoodItemDTOs representing all food items in the database.
     */
    @Transactional(readOnly = true)
    public List<FoodItemDTO> getAllFoodItems() {
        return foodItemRepository.findAll().stream()
                .map(foodItem -> new FoodItemDTO(foodItem.getId(), foodItem.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Cleans up all food items from the database before the application shuts down.
     * This method ensures that food items are deleted when the application is stopped.
     */
    @PreDestroy
    public void cleanup() {
        log.info("Cleaning up food items from the database...");
        foodItemRepository.deleteAll();
        log.info("All food items have been deleted.");
    }

    /**
     * Saves a list of FoodItem entities to the database.
     *
     * @param foodItems The list of FoodItem entities to be saved.
     */
    @Transactional
    public void saveAllFoodItems(List<FoodItem> foodItems) {
        foodItemRepository.saveAll(foodItems);
        log.info("Food items saved.");
    }
}