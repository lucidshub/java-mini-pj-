# CampusFind — Java Mini Project (JSP + Servlet + JDBC)

Lost-and-found web app for campus. Built with **Java, JSP, Servlets, HTML/CSS, JDBC (H2 file DB)** — as required for college mini-project.

Converted from Node/React version (`abdulahad-m07/campusfind`) to pure Java.

## Features
- Home, Browse + search (`?q=`), filter lost/found
- Report lost/found item (login required, photo upload max 2MB)
- Item details + contact info
- Claim item (reporter or faculty only)
- Login / Register:
  - Student: 9-digit PRN e.g. `123456789`
  - Faculty: `@acpce.ac.in` email
- H2 file database auto-created (`./campusfind_db.mv.db`), seeded with 3 demo items
- Uploads saved in `./uploads/` (no Cloudinary needed for demo)

## Tech (syllabus-friendly)
- Java 17, Maven, Jakarta Servlets 6, JSP + JSTL
- Embedded Tomcat 10.1 (no Tomcat install needed)
- H2 + plain JDBC (`DB.java`, `UserDao.java`, `ItemDao.java`)
- BCrypt passwords (`jbcrypt`)
- WAR also deployable to standalone Tomcat

## Run on YOUR laptop / FRIEND's laptop (same steps, each runs their own copy)

Prerequisites (one-time per laptop):
1. **Java 17+** — check: `java -version`. If missing: https://adoptium.net/temurin/releases (pick JDK 17, your OS)
2. **Maven 3.8+** — check: `mvn -version`. If missing: https://maven.apache.org/download.cgi (Mac: `brew install maven`)
3. **Git** — https://git-scm.com/downloads

Steps:
```bash
git clone https://github.com/lucidshub/java-mini-pj-.git campusfind-java
cd campusfind-java
chmod +x run.sh   # mac/Linux only (skip on Windows)
./run.sh
# open http://localhost:8080
```
Windows (no bash): run the two commands `run.sh` performs:
```
mvn package -DskipTests
java -cp "target\classes;target\classpath.txt" com.campusfind.App
```
(Note: on Windows generate the classpath first: `mvn dependency:build-classpath -Dmdep.outputFile=target/classpath.txt -Dmdep.pathSeparator=";"`)
Each laptop gets its own database (`campusfind_db.mv.db` is git-ignored, auto-created with 3 demo items).

## Friend on same WiFi (LAN demo — no deployment needed)
1. Connect both laptops to SAME WiFi/hotspot.
2. Run the app on your laptop.
3. Find your LAN IP: `ipconfig getifaddr en0` (mac) → e.g. `192.168.1.5`
4. Friend opens: `http://192.168.1.5:8080`
5. If blocked: allow Java in Firewall, same network required.

## WAR deploy (optional, college server)
- `target/campusfind.war` → copy to Tomcat `webapps/` → open `/campusfind`

## Project structure
```
pom.xml
src/main/java/com/campusfind/
  App.java              # embedded Tomcat launcher (0.0.0.0:8080)
  db/DB.java            # JDBC + H2 init/seed
  model/User.java, Item.java
  dao/UserDao.java, ItemDao.java
  util/Auth.java
  web/HomeServlet, BrowseServlet, ItemServlet, ReportServlet, ClaimServlet, AuthServlet, UploadDir.java
src/main/webapp/
  WEB-INF/jsp/*.jsp     # home, browse, report, details, login, register
  WEB-INF/web.xml
  css/style.css
```
