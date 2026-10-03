package com.campusfind;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

/**
 * Embedded Tomcat launcher.
 * Run:  ./run.sh   (or: mvn package, then java -cp "target/classes:$(cat target/classpath.txt)" com.campusfind.App)
 * Binds 0.0.0.0:8080 so a friend on the SAME WiFi can open http://YOUR-IP:8080
 */
public class App {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        String portEnv = System.getenv("PORT");
        if (portEnv != null) { try { port = Integer.parseInt(portEnv); } catch (Exception ignored) {} }
        if (args.length > 0) { try { port = Integer.parseInt(args[0]); } catch (Exception ignored) {} }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector(); // force default connector creation (binds 0.0.0.0 by default)

        String webappDir = findWebappDir();
        System.out.println("Using webapp dir: " + new File(webappDir).getAbsolutePath());
        cleanDuplicatedEmbedJars(webappDir);

        Context ctx = tomcat.addWebapp("", new File(webappDir).getAbsolutePath());
        // NOTE: do NOT manually add JasperInitializer - tomcat-embed-jasper
        // on the SYSTEM classpath registers itself. It must NOT also be in
        // WEB-INF/lib (that causes ClassCastException). EL impl (tomcat-embed-el)
        // MUST stay in WEB-INF/lib so JSP compilation finds it.

        new File("uploads").mkdirs();

        tomcat.start();
        String ip = lanIp();
        System.out.println("\n==============================================");
        System.out.println(" CampusFind (Java JSP/Servlet) running!");
        System.out.println(" Local : http://localhost:" + port);
        System.out.println(" LAN   : http://" + ip + ":" + port + "  <- friend opens this (same WiFi)");
        System.out.println(" Stop  : Ctrl+C");
        System.out.println("==============================================\n");
        tomcat.getServer().await();
    }

    /**
     * maven-war-plugin staging dir (target/campusfind) contains ALL deps,
     * but the WAR excludes embedded-Tomcat core/jasper (packagingExcludes).
     * If they remain in the exploded dir, JasperInitializer loads twice ->
     * ClassCastException. Delete them here (they come back on next package,
     * which is fine - we delete every startup). KEEP tomcat-embed-el:
     * JSP EL compilation needs it inside the webapp.
     */
    private static void cleanDuplicatedEmbedJars(String webappDir) {
        File lib = new File(webappDir + "/WEB-INF/lib");
        File[] files = lib.listFiles();
        if (files == null) return;
        String[] dropPrefixes = {"tomcat-embed-core-", "tomcat-embed-jasper-", "ecj-", "tomcat-annotations-api-"};
        for (File f : files) {
            for (String p : dropPrefixes) {
                if (f.getName().startsWith(p)) {
                    System.out.println("Removing duplicated embed jar from webapp: " + f.getName());
                    f.delete();
                }
            }
        }
    }

    /** Best-effort LAN IPv4 (e.g. 192.168.x.x) so a friend on the same WiFi can connect. */
    private static String lanIp() {
        try {
            var ifaces = java.net.NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                var ni = ifaces.nextElement();
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;
                var addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    var a = addrs.nextElement();
                    if (a instanceof java.net.Inet4Address && a.isSiteLocalAddress()) {
                        return a.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {}
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ignored) {}
        return "YOUR-IP";
    }

    private static String findWebappDir() {
        // 1) exploded WAR (has WEB-INF/lib with Jasper) - best for `mvn package` + run
        if (new File("target/campusfind").exists()) return "target/campusfind";
        // 2) dev mode: src/main/webapp (JSP works only if Jasper visible; prefer package first)
        if (new File("src/main/webapp").exists()) return "src/main/webapp";
        // 2) running from repo root after package
        if (new File("java mini pj/src/main/webapp").exists()) return "java mini pj/src/main/webapp";
        // 3) extracted webapp next to jar
        if (new File("webapp").exists()) return "webapp";
        return "src/main/webapp";
    }
}
