# CampusFind — Technical Architecture (Java implementation)

## 1. Stack

- Language: Java 17, build: Maven 3.8+
- Web: Jakarta Servlets 6 + JSP + JSTL, HTML5/CSS (no frontend framework)
- Server: Embedded Apache Tomcat 10.1, port 8080, binds 0.0.0.0 (LAN-ready)
- Database: H2 file database (`./campusfind_db.mv.db`), plain JDBC via DAO classes
- Passwords: BCrypt (jbcrypt). Auth: server-side HttpSession
- Packaging: WAR (`target/campusfind.war`), also deployable to standalone Tomcat
- Run: `./run.sh` (packages with Maven, then launches `com.campusfind.App`)

## 2. Request Flow

```text
Browser
  → Embedded Tomcat (port 8080)
    → Servlet (controller: validates, checks session)
      → DAO (plain JDBC query/update)
        → H2 file database  +  ./uploads/ folder (images)
    → JSP view renders HTML + CSS
```

Static CSS is served from `/css/`, uploaded images from `/uploads/<file>`.

## 3. Code Map

| File | Job |
|---|---|
| `com.campusfind.App` | Starts Tomcat, picks webapp dir (`target/campusfind`), removes duplicated embed jars, prints localhost + LAN URLs |
| `db/DB.java` | JDBC connection, `CREATE TABLE IF NOT EXISTS` for users/items, inserts 3 demo seed items |
| `model/User.java`, `model/Item.java` | Plain data holders (id, fields, `isLost()` / `isFaculty()` helpers) |
| `dao/UserDao.java` | `roleFromUsername` (9-digit PRN regex / `@acpce.ac.in` regex), register with BCrypt hash, login check |
| `dao/ItemDao.java` | `list(q)` with SQL `LIKE` search on name/description/location, `find(id)`, `create(...)`, `markClaimed(id, byUser)` |
| `util/Auth.java` | Session helpers: current user, login, logout |
| `web/HomeServlet` (`""`, `/home`) | Loads 6 newest items → `home.jsp` |
| `web/BrowseServlet` (`/browse`) | Applies `q` search + lost/found filter → `browse.jsp` |
| `web/ItemServlet` (`/item`) | Loads one item + `canClaim` flag (reporter or faculty) → `details.jsp` |
| `web/ReportServlet` (`/report`) | Login required. `@MultipartConfig` 2 MB. Validates fields, accepts images only, writes file to uploads, inserts row, redirects to new item page |
| `web/ClaimServlet` (`/claim`) | POST only. Verifies reporter-or-faculty server-side, marks claimed |
| `web/AuthServlet` (`/login`, `/register`, `/logout`) | Form handling, validation errors forwarded back to the form |
| `web/UploadDir` | Resolves a writable uploads dir + serves `/uploads/*` |
| JSPs (`WEB-INF/jsp/`) | `header`, `footer`, `home`, `browse`, `report`, `details`, `login`, `register` |

## 4. Key Design Decisions (viva-ready)

1. **H2 file DB** — zero-setup demo; every clone gets its own database (file is git-ignored); schema auto-created on first run.
2. **Plain JDBC + DAO** — syllabus topic; no ORM magic to explain in viva.
3. **Embedded Tomcat** — no server installation; the same WAR also deploys on standalone Tomcat.
4. **Session auth, not JWT** — simple and syllabus-friendly.
5. **Claim checked server-side** — the button is hidden for others AND the servlet re-checks, so it can't be bypassed.
6. **WAR excludes `tomcat-embed-core`/`jasper`** — bundling them loads Jasper twice and crashes with `ClassCastException`; `tomcat-embed-el` + GlassFish EL stay in so JSP compiles.

## 5. Demo / Deployment

- Local: `./run.sh` → `http://localhost:8080`
- Friend on same WiFi: `http://<host-LAN-IP>:8080` (the script prints the real IP)
- Fresh clone auto-seeds 3 demo items (wallet, bottle, calculator)
- Optional: copy `target/campusfind.war` to a Tomcat server's `webapps/` folder
