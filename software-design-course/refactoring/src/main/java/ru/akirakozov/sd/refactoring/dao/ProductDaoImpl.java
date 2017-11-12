package ru.akirakozov.sd.refactoring.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDaoImpl implements ProductDao {
    public ProductDaoImpl() {
        init();
    }

    @Override
    public boolean addProduct(Product product) {
        String sqlQuery = "INSERT INTO PRODUCT " +
                "(NAME, PRICE) VALUES (\"" + product.name + "\"," + product.price + ")";
        try {
            return execStatement(statement -> statement.executeUpdate(sqlQuery) > 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<List<Product>> getProducts() {
        return execQuery(
                "SELECT * FROM PRODUCT",
                ProductDaoImpl::parseProducts
        );
    }

    @Override
    public Optional<Product> getMax() {
        return execQuery(
                "SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1",
                ProductDaoImpl::parseProduct
        );
    }

    @Override
    public Optional<Product> getMin() {
        return execQuery(
                "SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1",
                ProductDaoImpl::parseProduct
        );
    }

    @Override
    public Optional<Long> getSum() {
        return execQuery(
                "SELECT SUM(price) FROM PRODUCT",
                row -> row.getLong(1)
        );
    }

    @Override
    public Optional<Long> getCount() {
        return execQuery(
                "SELECT COUNT(*) FROM PRODUCT",
                row -> row.getLong(1)
        );
    }

    private <T> Optional<T> execQuery(String sqlQuery, SqlRowMapper<T> mapper) {
        return execStatement(statement -> {
            try (ResultSet resultSet = statement.executeQuery(sqlQuery)) {
                if (resultSet.next()) {
                    return Optional.of(mapper.map(resultSet));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Optional.empty();
        });
    }

    private <T> T execStatement(StatementExecutor<T> executor) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:test.db");
             Statement statement = connection.createStatement()
        ) {
            return executor.execute(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() {
        String sql = "CREATE TABLE IF NOT EXISTS PRODUCT" +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " NAME           TEXT    NOT NULL, " +
                " PRICE          INT     NOT NULL)";
        execStatement(statement -> statement.executeUpdate(sql));
    }

    private static List<Product> parseProducts(ResultSet resultSet) throws SQLException {
        List<Product> products = new ArrayList<>();
        do {
            products.add(parseProduct(resultSet));
        } while (resultSet.next());
        return products;
    }

    private static Product parseProduct(ResultSet row) throws SQLException {
        return Product.create(row.getString("name"), row.getLong("price"));
    }

    @FunctionalInterface
    private interface SqlRowMapper<T> {
        T map(ResultSet row) throws SQLException;
    }

    @FunctionalInterface
    private interface StatementExecutor<T> {
        T execute(Statement statement) throws SQLException;
    }
}
