package com.campusfind.web;

import com.campusfind.dao.ItemDao;
import com.campusfind.model.Item;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/browse")
public class BrowseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String q = req.getParameter("q");
        String filter = req.getParameter("type");
        if (filter == null) filter = "all";
        try {
            List<Item> items = ItemDao.list(q);
            if (!"all".equals(filter)) {
                List<Item> f = new ArrayList<>();
                for (Item i : items) if (filter.equals(i.type)) f.add(i);
                items = f;
            }
            req.setAttribute("items", items);
            req.setAttribute("q", q == null ? "" : q);
            req.setAttribute("filter", filter);
            req.getRequestDispatcher("/WEB-INF/jsp/browse.jsp").forward(req, res);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
