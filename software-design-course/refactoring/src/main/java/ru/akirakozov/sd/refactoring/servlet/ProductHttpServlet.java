package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.dao.ProductDao;

import javax.servlet.http.HttpServlet;

class ProductHttpServlet extends HttpServlet {
    final ProductDao dao;

    ProductHttpServlet(ProductDao dao) {
        this.dao = dao;
    }
}
