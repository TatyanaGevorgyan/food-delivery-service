package com.epam.training.food.service;

import com.epam.training.food.data.DataStore;
import com.epam.training.food.domain.Cart;
import com.epam.training.food.domain.Credentials;
import com.epam.training.food.domain.Customer;
import com.epam.training.food.domain.Food;
import com.epam.training.food.domain.Order;
import com.epam.training.food.domain.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DefaultFoodDeliveryService implements FoodDeliveryService {
    private final DataStore dataStore;

    public DefaultFoodDeliveryService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public Customer authenticate(Credentials credentials) throws AuthenticationException {
        return dataStore.getCustomers().stream()
                .filter(c -> Objects.equals(c.getUserName(), credentials.getUserName())
                        && Objects.equals(c.getPassword(), credentials.getPassword()))
                .findFirst()
                .orElseThrow(() -> new AuthenticationException("Authentication failed"));
    }

    @Override
    public List<Food> listAllFood() {
        return dataStore.getFoods();
    }

    @Override
    public void updateCart(Customer customer, Food food, int pieces) throws LowBalanceException {
        validatePieces(pieces);

        Cart cart = customer.getCart();
        List<OrderItem> items = cart.getOrderItems();

        Optional<OrderItem> existingItemOpt = findItemInCart(items, food);

        if (pieces == 0 && existingItemOpt.isEmpty()) {
            throw new IllegalArgumentException("Food not in cart, cannot set pieces to 0");
        }

        BigDecimal projectedCartPrice = calculateProjectedCartPrice(items, existingItemOpt, food, pieces);
        validateBalance(customer, projectedCartPrice, food);

        applyCartChanges(cart, items, existingItemOpt, food, pieces, projectedCartPrice);
    }

    private void validatePieces(int pieces) {
        if (pieces < 0) {
            throw new IllegalArgumentException("Pieces cannot be negative");
        }
    }

    private Optional<OrderItem> findItemInCart(List<OrderItem> items, Food food) {
        return items.stream()
                .filter(item -> item.getFood().getName().equalsIgnoreCase(food.getName()))
                .findFirst();
    }

    private BigDecimal calculateProjectedCartPrice(List<OrderItem> items, Optional<OrderItem> existingItemOpt, Food food, int pieces) {
        BigDecimal currentTotal = items.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal existingPrice = existingItemOpt.map(OrderItem::getPrice).orElse(BigDecimal.ZERO);
        BigDecimal newItemPrice = food.getPrice().multiply(BigDecimal.valueOf(pieces));

        return currentTotal.subtract(existingPrice).add(newItemPrice);
    }

    private void validateBalance(Customer customer, BigDecimal projectedPrice, Food food) {
        if (projectedPrice.compareTo(customer.getBalance()) > 0) {
            throw new LowBalanceException("Unable to add current order for " + food + ", as with current cart content it would exceed available balance!");
        }
    }

    private void applyCartChanges(Cart cart, List<OrderItem> items, Optional<OrderItem> existingItemOpt, Food food, int pieces, BigDecimal newCartPrice) {
        if (pieces == 0) {
            items.remove(existingItemOpt.get());
        } else if (existingItemOpt.isPresent()) {
            OrderItem item = existingItemOpt.get();
            item.setPieces(pieces);
            item.setPrice(food.getPrice().multiply(BigDecimal.valueOf(pieces)));
        } else {
            BigDecimal itemPrice = food.getPrice().multiply(BigDecimal.valueOf(pieces));
            OrderItem newItem = new OrderItem.Builder()
                    .food(food)
                    .pieces(pieces)
                    .price(itemPrice)
                    .build();
            items.add(newItem);
        }
        cart.setPrice(newCartPrice);
    }

    @Override
    public Order createOrder(Customer customer) throws IllegalStateException {
        Cart cart = customer.getCart();
        if (cart.getOrderItems() == null || cart.getOrderItems().isEmpty()) {
            throw new IllegalStateException("Cannot create order: cart is empty");
        }

        Order order = new Order(customer);
        dataStore.createOrder(order);

        customer.setBalance(customer.getBalance().subtract(order.getPrice()));
        customer.getOrders().add(order);
        customer.setCart(Cart.getEmptyCart());

        return order;
    }
}