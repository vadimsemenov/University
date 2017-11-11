package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.dao.Product;
import ru.akirakozov.sd.refactoring.dao.ProductDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author akirakozov
 */
public class AddProductServlet extends ProductHttpServlet {
    public AddProductServlet(ProductDao dao) {
        super(dao);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        long price = Long.parseLong(request.getParameter("price"));
        Product product = Product.create(name, price);

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(dao.addProduct(product) ? "OK" : "FAIL");
    }
}
