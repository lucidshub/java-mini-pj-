package com.campusfind.web;

import com.campusfind.dao.ItemDao;
import com.campusfind.model.Item;
import com.campusfind.model.User;
import com.campusfind.util.Auth;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/item")
public class ItemServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        try {
            long id = Long.parseLong(idStr == null ? "0" : idStr);
            Item item = ItemDao.find(id);
            if (item == null) {
                res.sendError(404, "Item not found");
                return;
            }
            User me = Auth.current(req);
            boolean canClaim = me != null
                    && ((item.reporterId != null && item.reporterId == me.id) || me.isFaculty());
            req.setAttribute("item", item);
            req.setAttribute("canClaim", canClaim);
            req.getRequestDispatcher("/WEB-INF/jsp/details.jsp").forward(req, res);
        } catch (NumberFormatException e) {
            res.sendError(404, "Item not found");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
