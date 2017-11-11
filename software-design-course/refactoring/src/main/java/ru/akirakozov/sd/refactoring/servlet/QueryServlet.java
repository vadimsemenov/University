package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.dao.ProductDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                response.getWriter().println("Unknown command: " + command);
                break;
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
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

    private <T> void execAndWrite(HttpServletResponse response, String h1, Supplier<Optional<T>> supplier, String onEmpty) {
        try {
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>" + h1 + "</h1>");

            Optional<T> max = supplier.get();
            response.getWriter().println(max.map(Object::toString).orElse(onEmpty));
            response.getWriter().println("</br>");

            response.getWriter().println("</body></html>");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
