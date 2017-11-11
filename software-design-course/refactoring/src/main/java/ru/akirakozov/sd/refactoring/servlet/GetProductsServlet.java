package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.dao.Product;
import ru.akirakozov.sd.refactoring.dao.ProductDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<List<Product>> products = dao.getProducts();
        response.getWriter().println("<html><body>");
        if (!products.isPresent()) {
            response.getWriter().println("No products for 'getProducts' request");
        } else {
            for (Product product : products.get()) {
                response.getWriter().print(product);
                response.getWriter().println("</br>");
            }
        }
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
