package com.campusfind.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;

/** Resolves a writable uploads dir that works both in WAR and embedded-jar mode. */
public class UploadDir {
    public static String resolve(jakarta.servlet.ServletContext ctx) {
        // 1) explicit folder next to DB (best for jar mode + LAN demo)
        File local = new File("uploads");
        if (local.exists() || local.mkdirs()) {
            if (local.isDirectory() && local.canWrite()) return local.getAbsolutePath();
        }
        // 2) inside webapp (classic Tomcat WAR)
        try {
            String real = ctx.getRealPath("/uploads");
            if (real != null) return real;
        } catch (Exception ignored) {}
        // 3) temp fallback
        File tmp = new File(System.getProperty("java.io.tmpdir"), "campusfind-uploads");
        tmp.mkdirs();
        return tmp.getAbsolutePath();
    }

    @WebServlet("/uploads/*")
    public static class Serve extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, jakarta.servlet.http.HttpServletResponse res)
                throws jakarta.servlet.ServletException, java.io.IOException {
            String path = req.getPathInfo();
            if (path == null || path.contains("..")) { res.sendError(404); return; }
            java.util.List<String> candidates = new java.util.ArrayList<>();
            candidates.add(new File("uploads", path.replaceFirst("^/", "")).getAbsolutePath());
            try {
                String real = getServletContext().getRealPath("/uploads" + path);
                if (real != null) candidates.add(real);
            } catch (Exception ignored) {}
            candidates.add(new File(System.getProperty("java.io.tmpdir"), "campusfind-uploads" + path).getAbsolutePath());
            for (String p : candidates) {
                File f = new File(p);
                if (f.isFile()) {
                    String ct = getServletContext().getMimeType(f.getName());
                    if (ct == null) ct = "application/octet-stream";
                    res.setContentType(ct);
                    java.nio.file.Files.copy(f.toPath(), res.getOutputStream());
                    return;
                }
            }
            res.sendError(404);
        }
    }
}
