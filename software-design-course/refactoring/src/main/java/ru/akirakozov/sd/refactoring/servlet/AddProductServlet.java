package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.dao.Product;
import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.http.HttpBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author akirakozov
 */
public class AddProductServlet extends ProductHttpServlet {
    public AddProductServlet(ProductDao dao) {
        super(dao);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        long price = Long.parseLong(request.getParameter("price"));
        Product product = Product.create(name, price);

        HttpBuilder.createOpen()
                .appendLine(dao.addProduct(product) ? "OK" : "FAIL")
                .writeTo(response);
    }
}
