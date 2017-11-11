package ru.akirakozov.sd.refactoring.dao;

public class Product {
    final String name;
    final long price;

    public static Product create(String name, long price) {
        return new Product(name, price);
    }

    private Product(String name, long price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return name + '\t' + price;
    }
}
