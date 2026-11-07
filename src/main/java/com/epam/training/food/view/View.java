package com.epam.training.food.view;

import com.epam.training.food.domain.Credentials;
import com.epam.training.food.domain.Customer;
import com.epam.training.food.domain.Food;
import com.epam.training.food.domain.Order;
import com.epam.training.food.values.FoodSelection;

import java.util.List;

public interface View {
    Credentials readCredentials();
    void showWelcomeMessage(Customer customer);
    void showFoods(List<Food> foods);
    FoodSelection readFoodSelection();
    void showAddedToCart(FoodSelection selection);
    void showOrderDetails(Order order);
    void showErrorMessage(String message);
}