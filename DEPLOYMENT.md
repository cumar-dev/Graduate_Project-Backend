# Deploy backend to Render

Use this when your **GitHub repo contains only the backend** (this folder is the repo root).

## 1. MongoDB Atlas

1. Create a cluster at [MongoDB Atlas](https://www.mongodb.com/cloud/atlas).
2. **Database Access** → user + password.
3. **Network Access** → allow `0.0.0.0/0`.
4. Copy the connection string (Drivers):

```text
mongodb+srv://USER:PASSWORD@cluster0.xxxxx.mongodb.net/graduate_project?retryWrites=true&w=majority
```

## 2. Render — Web Service

Render’s UI often **does not list Java**. Use **Docker** instead (this repo includes a `Dockerfile`).

1. [dashboard.render.com](https://dashboard.render.com) → **New** → **Web Service**.
2. Connect your **backend** GitHub repository (`Graduate_Project-Backend`).
3. Settings:

| Setting | Value |
|---------|--------|
| **Root Directory** | *(leave empty)* |
| **Language / Runtime** | **Docker** (not Node, not Python) |
| **Dockerfile Path** | `Dockerfile` (default at repo root) |
| **Build & Start commands** | *(leave empty — Docker handles both)* |
| **Health Check Path** | `/api/health` |

Or use **Blueprint** → import `render.yaml` from this repo.

> **Do not** pick Node/Python/Ruby — Spring Boot needs **Docker** on Render.

## 3. Environment variables (Render → Environment)

| Key | Value |
|-----|--------|
| `MONGODB_URI` | Your full Atlas URI |
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `APP_CORS_ORIGINS` | Your Vercel URL(s), e.g. `https://your-app.vercel.app,https://*.vercel.app` |
| `JAVA_VERSION` | `17` |

Deploy and copy your API URL, e.g. `https://siu-graduate-api.onrender.com`.

## 4. Verify

Open in browser:

```text
https://YOUR-SERVICE.onrender.com/api/health
```

Expected: `{"status":"ok"}`

## 5. Connect the frontend

In your **Vercel** project (frontend repo), set:

```text
VITE_API_BASE_URL=https://YOUR-SERVICE.onrender.com/api
```

Then update `APP_CORS_ORIGINS` on Render with the exact Vercel URL and **redeploy** the backend if login fails.

## Notes

- Free Render sleeps after ~15 min idle; first request may take 30–60 seconds.
- Demo users seed on first start: `student@university.edu` / `student123`, etc.
- Never commit `backend/.env` — use Render env vars only in production.
