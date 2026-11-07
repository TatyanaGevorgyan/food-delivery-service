package com.epam.training.food.data;

import com.epam.training.food.domain.Order;
import com.epam.training.food.domain.OrderItem;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class OrderWriter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void writeOrders(List<Order> orders, String outputFile) {
        try (var fileWriter = new FileWriter(outputFile)) {
            fileWriter.write(addOrders(orders));
        } catch (IOException e) {
            throw new RuntimeException("IOException happened while writing order file: " + outputFile, e);
        }
    }

    private String addOrders(List<Order> orders) {
        return orders
                .stream()
                .map(order -> addOrderItems(
                        order.getOrderItems(),
                        order.getPrice(),
                        order.getTimestampCreated(),
                        order.getOrderId(),
                        order.getCustomerId()
                ))
                .collect(Collectors.joining("\n"));
    }

    private String addOrderItems(List<OrderItem> orderItems, BigDecimal totalPrice, LocalDateTime timestampCreated, Long orderId, long customerId) {
        return orderItems
                .stream()
                .map(orderItem -> addOrderItem(
                        orderItem,
                        totalPrice,
                        timestampCreated,
                        orderId,
                        customerId
                ))
                .collect(Collectors.joining("\n"));
    }

    private String addOrderItem(OrderItem orderItem, BigDecimal totalPrice, LocalDateTime timestampCreated, Long orderId, long customerId) {
        return String.join(
                ",",
                Long.toString(orderId),
                Long.toString(customerId),
                orderItem.getFood().getName(),
                Integer.toString(orderItem.getPieces()),
                orderItem.getPrice().toPlainString(),
                timestampCreated.format(DATE_TIME_FORMATTER),
                totalPrice.toPlainString());
    }
}
