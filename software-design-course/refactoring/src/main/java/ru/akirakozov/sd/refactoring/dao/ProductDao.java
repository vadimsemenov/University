package ru.akirakozov.sd.refactoring.dao;

import java.util.List;
import java.util.Optional;

public interface ProductDao {
    boolean addProduct(Product product);
    Optional<List<Product>> getProducts();
    Optional<Product> getMax();
    Optional<Product> getMin();
    Optional<Long> getCount();
    Optional<Long> getSum();
}
