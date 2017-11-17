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

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return price == product.price && (name != null ? name.equals(product.name) : product.name == null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (int) (price ^ (price >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return name + '\t' + price;
    }
}
