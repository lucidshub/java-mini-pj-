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

@WebServlet("/claim")
public class ClaimServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User me = Auth.current(req);
        if (me == null) {
            res.sendRedirect(req.getContextPath() + "/login?from=/browse");
            return;
        }
        try {
            long id = Long.parseLong(req.getParameter("id"));
            Item item = ItemDao.find(id);
            if (item == null) { res.sendError(404); return; }
            boolean allowed = (item.reporterId != null && item.reporterId == me.id) || me.isFaculty();
            if (!allowed) { res.sendError(403, "Only reporter or faculty can claim"); return; }
            ItemDao.markClaimed(id, me.id);
            res.sendRedirect(req.getContextPath() + "/item?id=" + id + "&claimed=1");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
