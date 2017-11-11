package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.dao.Product;
import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.http.HttpBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends ProductHttpServlet {
    public GetProductsServlet(ProductDao dao) {
        super(dao);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        Optional<List<Product>> products = dao.getProducts();
        HttpBuilder builder = HttpBuilder.createOpen();

        if (!products.isPresent()) {
            builder.appendLine("No products for 'getProducts' request");
        } else {
            for (Product product : products.get()) {
                builder.appendLine(product.toString());
            }
        }
        builder.writeTo(response);
    }
}
