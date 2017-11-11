package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.dao.ProductDao;
import ru.akirakozov.sd.refactoring.http.HttpBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author akirakozov
 */
public class QueryServlet extends ProductHttpServlet {
    public QueryServlet(ProductDao dao) {
        super(dao);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String command = request.getParameter("command");
        switch (command) {
            case "max":
                getMax(response);
                break;
            case "min":
                getMin(response);
                break;
            case "sum":
                getSum(response);
                break;
            case "count":
                getCount(response);
                break;
            default:
                HttpBuilder.createOpen()
                        .appendLine("Unknown command: " + command)
                        .writeTo(response);
                break;
        }
    }

    private void getMax(HttpServletResponse response) {
        execAndWrite(response, "Product with max price: ", dao::getMax, "No products for 'max' request");
    }

    private void getMin(HttpServletResponse response) {
        execAndWrite(response, "Product with min price: ", dao::getMin, "No products for 'min' request");
    }

    private void getSum(HttpServletResponse response) {
        execAndWrite(response, "Summary price: ", dao::getSum, "No products for 'sum' request");
    }

    private void getCount(HttpServletResponse response) {
        execAndWrite(response, "Number of products: ", dao::getCount, "No products for 'count' request");
    }

    private <T> void execAndWrite(HttpServletResponse response, String header, Supplier<Optional<T>> supplier, String onEmpty) {
        Optional<T> max = supplier.get();
        HttpBuilder.createOpen()
                .appendHeader(header)
                .appendLine(max.map(Object::toString).orElse(onEmpty))
                .writeTo(response);
    }
}
