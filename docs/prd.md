# CampusFind — Product Requirements Document

## 1. Product Overview

CampusFind is a web-based lost-and-found platform for college campuses.

> A student loses or finds something → they post it → another student finds the post → they contact each other → the item is returned.

The product is a simple digital campus notice board for A.C. Patil College of Engineering.

## 2. Users and Roles

| Role | How they register | Powers |
|---|---|---|
| Student | 9-digit PRN (e.g. `123456789`) + name + password | Browse, search, report, view details, claim OWN items |
| Faculty | `@acpce.ac.in` email + name + password | Everything above + claim ANY item (moderation) |

Passwords are BCrypt-hashed. Sessions are server-side (HttpSession).

## 3. Pages

- **Home (`/`)** — branding, search bar, 6 most recent items, Report button
- **Browse (`/browse`)** — all active items, text search (`?q=`), All/Lost/Found filter
- **Report (`/report`)** — login required. Fields: type (lost/found), item name, description, location, date, photo (optional, max 2MB, images only), contact
- **Item Details (`/item?id=`)** — photo, name, type badge, description, location, date, contact, reporter name; Claim button visible only to reporter or faculty; claimed items show a "Claimed" notice
- **Login (`/login`) / Register (`/register`)** — with validation errors (bad PRN/email rejected, duplicate username rejected, min 4-char password)

## 4. Functional Requirements

- FR-01 Browse: list all unclaimed items, newest first
- FR-02 Search: case-insensitive match on item name, description, location
- FR-03 Report: validated submit, saved with reporter id/name/role
- FR-04 Image upload: optional, stored in `./uploads/`, served at `/uploads/<file>`
- FR-05 Details: per-item page with contact info
- FR-06 Claim: reporter-or-faculty only; claimed items hidden from all listings
- FR-07 Auth: register/login/logout, role derived from username format

## 5. Data Model

```text
users: id, username (unique), password (hash), name, role, created_at
items: id, type, item_name, description, location, item_date, image_url,
       contact, reporter_id, reporter_name, reporter_role,
       claimed, claimed_by, claimed_at, created_at
```

## 6. Out of Scope

Real-time chat, notifications, AI matching, ratings, payments,
multi-campus support, native mobile app, full admin dashboard
(faculty claim covers moderation).

## 7. Success Criteria

A student can: open site → search/browse → open details → contact poster.
And: login → report with photo → item appears in browse → reporter marks
claimed → it disappears from listings.
Works on desktop and mobile browsers, on localhost and over LAN.
