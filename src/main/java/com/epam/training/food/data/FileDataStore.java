package com.epam.training.food.data;

import com.epam.training.food.domain.Customer;
import com.epam.training.food.domain.Food;
import com.epam.training.food.domain.Order;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileDataStore implements DataStore {
    private final String inputFolderPath;
    private List<Customer> customers = new ArrayList<>();
    private List<Food> foods = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();

    public FileDataStore(String inputFolderPath) {
        this.inputFolderPath = inputFolderPath;
    }

    public void init() {
        CustomerReader customerReader = new CustomerReader();
        FoodReader foodReader = new FoodReader();

        String customersPath = Paths.get(inputFolderPath, "customers.csv").toString();
        String foodsPath = Paths.get(inputFolderPath, "foods.csv").toString();
        String ordersPath = Paths.get(inputFolderPath, "orders.csv").toString();

        this.customers = customerReader.read(customersPath);
        this.foods = foodReader.read(foodsPath);
        this.orders = new OrderReader(foods).read(ordersPath);

        for (Order order : orders) {
            customers.stream()
                    .filter(customer -> customer.getId() == order.getCustomerId())
                    .findFirst()
                    .ifPresent(customer -> customer.getOrders().add(order));
        }
    }

    @Override
    public List<Customer> getCustomers() {
        return customers;
    }

    @Override
    public List<Food> getFoods() {
        return foods;
    }

    @Override
    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public Order createOrder(Order order) {
        long nextId = orders.stream()
                .map(Order::getOrderId)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .max()
                .orElse(-1L) + 1;

        order.setOrderId(nextId);
        orders.add(order);
        writeOrders();
        return order;
    }

    @Override
    public void writeOrders() {
        OrderWriter orderWriter = new OrderWriter();
        String ordersPath = Paths.get(inputFolderPath, "orders.csv").toString();
        orderWriter.writeOrders(orders, ordersPath);
    }
}