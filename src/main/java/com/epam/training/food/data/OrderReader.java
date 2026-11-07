package com.epam.training.food.data;

import com.epam.training.food.domain.Food;
import com.epam.training.food.domain.Order;
import com.epam.training.food.domain.OrderItem;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderReader extends Reader<Order> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final List<Food> foods;

    public OrderReader(List<Food> foods) {
        this.foods = foods;
    }

    @Override
    public List<Order> read(String pathOfFile) {
        if (isMissingOrEmpty(Paths.get(pathOfFile))) {
            return new ArrayList<>();
        }
        Map<Long, List<String[]>> rowsByOrderId = new LinkedHashMap<>();
        for (String line : readFromFile(pathOfFile)) {
            if (line.isBlank()) {
                continue;
            }
            String[] columns = splitLineByComma(line);
            long orderId = Long.parseLong(columns[0]);
            rowsByOrderId.computeIfAbsent(orderId, id -> new ArrayList<>()).add(columns);
        }
        return rowsByOrderId.values()
                .stream()
                .map(this::buildOrder)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean isMissingOrEmpty(Path path) {
        try {
            return !Files.exists(path) || Files.size(path) == 0;
        } catch (IOException e) {
            throw new UncheckedIOException("IOException happened while checking file: " + path, e);
        }
    }

    private Order buildOrder(List<String[]> rows) {
        String[] first = rows.get(0);
        long orderId = Long.parseLong(first[0]);
        long customerId = Long.parseLong(first[1]);
        LocalDateTime timestamp = LocalDateTime.parse(first[5], DATE_TIME_FORMATTER);
        BigDecimal totalPrice = new BigDecimal(first[6]);
        List<OrderItem> items = rows.stream()
                .map(this::buildOrderItem)
                .collect(Collectors.toCollection(ArrayList::new));
        return new Order(orderId, customerId, items, totalPrice, timestamp);
    }

    private OrderItem buildOrderItem(String[] row) {
        Food food = foods.stream()
                .filter(f -> f.getName().equals(row[2]))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown food in orders file: " + row[2]));
        return new OrderItem(food, Integer.parseInt(row[3]), new BigDecimal(row[4]));
    }
}