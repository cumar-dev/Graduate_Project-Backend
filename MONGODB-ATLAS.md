# MongoDB Atlas setup

Local development and production both use **MongoDB Atlas** (no local MongoDB required).

---

## 1. One-time setup

1. Create a cluster at [MongoDB Atlas](https://cloud.mongodb.com).
2. **Database Access** → user (e.g. `Graduate_Project`) + password.
3. **Network Access** → allow `0.0.0.0/0`.
4. **Connect** → Drivers → copy URI and fix password + cluster host.

---

## 2. Local development

1. Copy the example env file:

```bat
copy backend\.env.example backend\.env
```

2. Edit `backend\.env` (one line):

```env
MONGODB_URI=mongodb+srv://USER:PASSWORD@cluster0.xxxxx.mongodb.net/graduate_project?retryWrites=true&w=majority
```

3. Start the app:

```bat
run-backend.bat
run-frontend.bat
```

Or `run.bat` for both.

Demo users are created on **first start** if the database is empty.

---

## 3. Render (production)

Same URI in Render → **Environment** → `MONGODB_URI`.

Also set `SPRING_PROFILES_ACTIVE=prod` and `APP_CORS_ORIGINS` for Vercel.

---

## Security

- `backend/.env` is gitignored — never commit it.
- Reset your Atlas password if it was shared publicly.
