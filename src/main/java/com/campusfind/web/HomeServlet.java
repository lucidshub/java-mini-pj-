package com.campusfind.web;

import com.campusfind.dao.ItemDao;
import com.campusfind.model.Item;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet({"", "/home"})
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            List<Item> items = ItemDao.list(null);
            req.setAttribute("recentItems", items.size() > 6 ? items.subList(0, 6) : items);
            req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, res);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
