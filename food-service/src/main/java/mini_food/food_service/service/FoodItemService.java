package mini_food.food_service.service;

import java.util.List;
import mini_food.food_service.dto.FoodItemRequest;
import mini_food.food_service.entity.FoodItem;
import mini_food.food_service.exception.ResourceNotFoundException;
import mini_food.food_service.repository.FoodItemRepository;
import org.springframework.stereotype.Service;

@Service
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;

    public FoodItemService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    public List<FoodItem> getAll(String category) {
        if (category == null || category.isBlank()) {
            return foodItemRepository.findAll();
        }
        return foodItemRepository.findByCategoryIgnoreCase(category);
    }

    public FoodItem getById(Long id) {
        return foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + id));
    }

    public FoodItem create(FoodItemRequest request) {
        FoodItem foodItem = new FoodItem();
        mapRequest(foodItem, request);
        return foodItemRepository.save(foodItem);
    }

    public FoodItem update(Long id, FoodItemRequest request) {
        FoodItem foodItem = getById(id);
        mapRequest(foodItem, request);
        return foodItemRepository.save(foodItem);
    }

    public void delete(Long id) {
        FoodItem foodItem = getById(id);
        foodItemRepository.delete(foodItem);
    }

    private void mapRequest(FoodItem foodItem, FoodItemRequest request) {
        foodItem.setName(request.getName());
        foodItem.setDescription(request.getDescription());
        foodItem.setPrice(request.getPrice());
        foodItem.setCategory(request.getCategory());
        foodItem.setImageUrl(request.getImageUrl());
        foodItem.setAvailable(request.getAvailable() != null ? request.getAvailable() : Boolean.TRUE);
    }
}
