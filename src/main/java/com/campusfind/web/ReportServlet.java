package com.campusfind.web;

import com.campusfind.dao.ItemDao;
import com.campusfind.model.User;
import com.campusfind.util.Auth;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@WebServlet("/report")
@MultipartConfig(maxFileSize = 2 * 1024 * 1024, maxRequestSize = 3 * 1024 * 1024)
public class ReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (Auth.current(req) == null) {
            res.sendRedirect(req.getContextPath() + "/login?from=/report");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/jsp/report.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User me = Auth.current(req);
        if (me == null) {
            res.sendRedirect(req.getContextPath() + "/login?from=/report");
            return;
        }
        try {
            String type = req.getParameter("type");
            String itemName = trim(req.getParameter("itemName"));
            String description = trim(req.getParameter("description"));
            String location = trim(req.getParameter("location"));
            String date = trim(req.getParameter("date"));
            String contact = trim(req.getParameter("contact"));

            if (!"lost".equals(type) && !"found".equals(type)) { error(req, res, "Type must be lost or found."); return; }
            if (isEmpty(itemName) || isEmpty(description) || isEmpty(location) || isEmpty(date) || isEmpty(contact)) {
                error(req, res, "All fields are required."); return;
            }

            // Optional image -> saved under <webapp>/uploads (also works in embedded mode via absolute path)
            String imageUrl = "";
            Part image;
            try { image = req.getPart("image"); } catch (Exception e) { image = null; }
            if (image != null && image.getSize() > 0) {
                String ct = image.getContentType();
                if (ct == null || !ct.startsWith("image/")) { error(req, res, "Only image files allowed."); return; }
                String ext = ".jpg";
                String submitted = Paths.get(image.getSubmittedFileName()).getFileName().toString();
                int dot = submitted.lastIndexOf('.');
                if (dot >= 0 && dot < submitted.length() - 1) {
                    String e2 = submitted.substring(dot).toLowerCase();
                    if (e2.matches("\\.(jpg|jpeg|png|gif|webp)")) ext = e2;
                }
                String fileName = "item-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16) + ext;
                String uploadDir = UploadDir.resolve(getServletContext());
                new File(uploadDir).mkdirs();
                image.write(uploadDir + File.separator + fileName);
                imageUrl = req.getContextPath() + "/uploads/" + fileName;
            }

            long id = ItemDao.create(type, itemName, description, location, date, contact, imageUrl,
                    me.id, me.name, me.role);
            res.sendRedirect(req.getContextPath() + "/item?id=" + id);
        } catch (Exception e) {
            error(req, res, "Could not save item: " + e.getMessage());
        }
    }

    private void error(HttpServletRequest req, HttpServletResponse res, String msg) throws ServletException, IOException {
        req.setAttribute("error", msg);
        req.getRequestDispatcher("/WEB-INF/jsp/report.jsp").forward(req, res);
    }
    private static String trim(String s) { return s == null ? "" : s.trim(); }
    private static boolean isEmpty(String s) { return s == null || s.trim().isEmpty(); }
}
