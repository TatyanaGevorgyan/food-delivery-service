package com.epam.training.food.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Customer extends Credentials {
    private final List<Order> orders = new ArrayList<>();
    private long id;
    private String name;
    private BigDecimal balance;
    private Cart cart;

    public Customer() {
    }

    public Customer(String userName, String password, long id, String name, BigDecimal balance, Cart cart) {
        super(userName, password);
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.cart = cart;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        Customer customer = (Customer) o;
        return id == customer.id && Objects.equals(name, customer.name) && Objects.equals(balance, customer.balance)
            && Objects.equals(orders, customer.orders) && Objects.equals(cart, customer.cart);
    }

    @Override public int hashCode() {
        return Objects.hash(super.hashCode(), id, name, balance, orders, cart);
    }

    @Override public String toString() {
        return "Customer{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", balance=" + balance +
            ", orders=" + orders +
            ", cart=" + cart +
            '}';
    }

    public static class Builder {
        private String userName;
        private String password;
        private long id;
        private String name;
        private BigDecimal balance;
        private List<Order> orders = new ArrayList<>();
        private Cart cart;

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder orders(List<Order> orders) {
            this.orders = orders;
            return this;
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Builder cart(Cart cart) {
            this.cart = cart;
            return this;
        }

        public Customer build() {
            Customer customer = new Customer(userName, password, id, name, balance, cart);
            customer.getOrders().addAll(orders);
            return customer;
        }
    }
}